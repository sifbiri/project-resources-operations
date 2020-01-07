(ns app.model.api
  (:require ;[com.wsscode.pathom.connect :as pc]
                                        ; [taoensso.timbre :as log]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [app.model.project-model :as project]
   [clojure.set :as s]
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   [clojure.data.json :as json]
                                        ;[datomic.client.api :as d]
   [datomic.api :as d]
   [clj-http.client :as client]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
                                        ;[clojure.data.generators :as gen]
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [app.model.database :as db :refer [conn]]
   [clj-time.core :as t]
   [clojure.core :refer :all])
  (:import datomic.Util))

(def project-keys [:project/id
                   :project/finish-date
                   #_:project/author-name  :project/start-date
                   :project/name
                   :project/created-date
                   ])

(def db-url "datomic:dev://localhost:4334/one2")




(d/create-database "datomic:dev://localhost:4334/one2")
(d/delete-database db-url)

(def conn2 (d/connect db-url))

;; DB stuff

                                        ;(def db-uri-base "datomic:mem://")

(def resource io/resource)

;; (defn scratch-conn
;;   "Create a connection to an anonymous, in-memory database."
;;   []
;;   (let [uri (str db-uri-base (d/squuid))]
;;     (d/delete-database uri)
;;     (d/create-database uri)
;;     (d/connect uri)))


;; (def conn (scratch-conn))

(defn read-all
  "Read all forms in f, where f is any resource that can
   be opened by io/reader"
  [f]
  (Util/readAll (io/reader f)))

(defn transact-all
  "Load and run all transactions from f, where f is any
   resource that can be opened by io/reader."
  [conn f]
  (loop [n 0
         [tx & more] (read-all f)]
    (if tx
      (recur (+ n (count (:tx-data  @(d/transact conn tx))))
             more)
      {:datoms n})))

(io/resource "edn/schema.edn")




(defn uuid [s]
  (assert (string? s))
  (java.util.UUID/fromString (.toLowerCase s)))



(def assignment-phased-keys
  '(:task/name
    :task/is-active
    :resource/id
    :assignment/modified-date
    :assignment/id
    :task/id
    :assignment/work
    :time/by-day
    :project/name
    :project/id
    ))

(def resource-keys
  '( :resource/id
     :resource/modified-date
     :resource/created-date
     :resource/email-address
                                        ;:resource/type
     :resource/name
     
     :resource/is-active
     ))


(def resources
  (as-> (-> (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/Resources"
                        (project/prepare-request-options))
            :body
            (json/read-str :key-fn csk/->kebab-case-keyword)
            :value
            ;; just to have one.
            ;; first
            
            ) response



    (cske/transform-keys
     (fn [x]
       (keyword (clojure.string/replace-first (name x) #"-" "/")) )
     response)

    (map #(select-keys % resource-keys) response)
    (map
     (fn [x] (update x :resource/created-date #(when-not (nil? %)
                                                 (clojure.instant/read-instant-date %))))
     response)

    (map
     (fn [x] (update x :resource/modified-date #(when-not (nil? %)
                                                  (clojure.instant/read-instant-date %))))
     response)


    (map (fn [x] (update x :resource/id #(uuid %))) response)
    
    ))



(defn get-resource
  [id]
  (first (filter #(= (:resource/id %)  id) resources)))



(defn remove-nils
  [m]
  (apply dissoc                                                                                            
         m                                                                                                  
         (for [[k v] m :when (nil? v)] k)))


(defn assignement-phased-project-id
  [id]
  (as-> (-> (client/get (str "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/AssignmentTimephasedDataSet?$filter=ProjectId eq guid'" id "'")
                        (project/prepare-request-options))
            :body
            (json/read-str :key-fn csk/->kebab-case-keyword)
            :value
            ;; just to have one.
            ;; first
            
            ) response

    (cske/transform-keys
     (fn [x]
       (keyword (clojure.string/replace-first (name x) #"-" "/")) )
     response)

    (map #(select-keys % assignment-phased-keys) response)

    (map
     (fn [x] (update x :assignment/work #(when-not (nil? %)
                                           (clojure.edn/read-string %))))
     response)

    (map
     (fn [x] (update x :time/by-day  #(when-not (nil? %)
                                        (clojure.instant/read-instant-date %))))
     response)

    (map
     (fn [x] (update x :assignment/modified-date
                     #(when-not (nil? %)
                        (clojure.instant/read-instant-date %))))
     response)

    (map #(s/rename-keys % {:time/by-day :assignment/by-day}) response)

    (map (fn [x] (update x :task/id #(uuid %))) response)
    (map (fn [x] (update x :project/id #(uuid %))) response)
    (map (fn [x] (update x :assignment/id #(uuid %))) response)
    (map (fn [x] (update x :resource/id #(uuid %))) response)
    
    (map (fn [x] (let [task-name (:task/name x)
                       task-id (:task/id x)
                       task-is-active (:task/is-active x )
                       resource (remove-nils (get-resource (:resource/id x)))]
                   
                   (remove-nils (assoc (dissoc x :task/name :task/id :task/is-active :project/id  :resource/id)
                                  :assignment/task (remove-nils {:task/name task-name
                                                                 :task/id task-id
                                                                 :task/is-active task-is-active})
                                  :assignment/resource resource)))) response)
    ))

(def all-project-names '("LeFrak"
                         "RBC - IVALUA - S2C"
                         "Louboutin US"
                         "Boys & Girls Club of America"
                         "Visteon (Xeeva)"
                         "simple"
                         "TESTPROJECT2"
                         "FLX NORAM Portfolio Master"
                         "Desjardins - Appro360"
                         "Revo - APA"
                         "OLIN BRASS - APA"
                         "NEOGEN - APA"
                         "Kraton - APA"
                         "Ivanhoe Cambridge - P2P"
                         "Ivanhoe Cambridge - TMA"
                         "Customers Support"
                         "Vacations"
                         "Cadillac Fairview - Quantum"
                         "COGECO - S2C - Wave 1"
                         "COGECO - Spend - Wave 2"
                         "COGECO - Supplier Performance - Wave 3"
                         "Other Projects"
                         "Training"
                         "Closed Projects"
                         "COGECO - LEGAL AFFAIRS"
                         "Cadillac Fairview - Enhancements and Reports"
                         "Vortex - CMS Activities"
                         "Bell - Enhancements"
                         "Ivalua - Subcontracting"
                         "Test 679"
                         "Opérations d’administration de feuilles de temps"))


(def selected-project-names #{"Louboutin US" "simple" "Desjardins - Appro360"})


(def simple-project-id #uuid "df2ebab7-0816-ea11-a08e-005056eb471c")

(defn get-all-projects
  []
  (let [response (-> (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/Projects"
                                 (project/prepare-request-options))
                     :body
                     (json/read-str :key-fn keyword)
                     :value)
        all-projects (map #(select-keys % [:Name :Id]) response)
        all-projects-renamed (mapv #(s/rename-keys % {:Name :project/name :Id :project/id}) all-projects)

        keyword-response (cske/transform-keys csk/->kebab-case-keyword response)
        splited-response (cske/transform-keys
                          (fn [x]
                            (keyword (clojure.string/replace-first (name x) #"-" "/")) )
                          keyword-response)
        result (map #(select-keys % project-keys) splited-response)
        result2 (map (fn [x] (update x :project/id #(uuid %)))result)
        result3 (map
                 (fn [x] (update x :project/start-date #(when-not (nil? %)
                                                          (clojure.instant/read-instant-date %))))
                 result2)
        result4 (map
                 (fn [x] (update x :project/finish-date #(when-not (nil? %)
                                                           (clojure.instant/read-instant-date %))))
                 result3)
        result5 (map
                 (fn [x] (update x :project/work #(when-not (nil? %)
                                                    (clojure.edn/read-string %))))
                 result4)
        result6
        (map
         (fn [x] (update x :project/created-date #(when-not (nil? %)

                                                    (clojure.instant/read-instant-date %))))
         result5)

        ;; adding assignement to project
        result7
        (map
         (fn [x] (let
                     [assignments (assignement-phased-project-id (str (:project/id x)))]

                   (remove-nils (assoc x :project/assignments (mapv (fn [x] (dissoc x :project/name))  assignments)))))
         result6)]
    
    result7)
  )


(def all-projects (get-all-projects))
(def selected-projects (filter (fn [x] (selected-project-names (:project/name x)))  all-projects))


(defn get-projects
  [name]
  (let [response (-> (client/get "https://flu.sharepoint.com/site"
                                 (project/prepare-request-options))
                     :body
                     (json/read-str :key-fn keyword)
                     :value)
        all-projects (map #(select-keys % [:Name :Id]) response)
        all-projects-renamed (mapv #(s/rename-keys % {:Name :project/name :Id :project/id}) all-projects)

        keyword-response (cske/transform-keys csk/->kebab-case-keyword response)
        splited-response (cske/transform-keys
                          (fn [x]
                            (keyword (clojure.string/replace-first (name x) #"-" "/")) )
                          keyword-response)
        result (map #(select-keys % project-keys) splited-response)
        result2 (map (fn [x] (update x :project/id #(uuid %)))result)
        result3 (map
                 (fn [x] (update x :project/start-date #(when-not (nil? %)
                                                          (clojure.instant/read-instant-date %))))
                 result2)
        result4 (map
                 (fn [x] (update x :project/finish-date #(when-not (nil? %)
                                                           (clojure.instant/read-instant-date %))))
                 result3)
        result5 (map
                 (fn [x] (update x :project/work #(when-not (nil? %)
                                                    (clojure.edn/read-string %))))
                 result4)
        result6
        (map
         (fn [x] (update x :project/created-date #(when-not (nil? %)

                                                    (clojure.instant/read-instant-date %))))
         result5)

        ;; adding assignement to project
        result7
        (map
         (fn [x] (let
                     [assignments (assignement-phased-project-id (str (:project/id x)))]

                   (remove-nils (assoc x :project/assignments (mapv (fn [x] (dissoc x :project/name))  assignments)))))
         result6)]
    
    result7))




#_(defn get-project3
  [name]
  (cske/transform-keys
   (fn [x]
     (keyword (clojure.string/replace-first (name x) #"-" "/")) )
   (get-project2 name)))

(def all-projects (get-all-projects))


(def simple-project (filter #(= (:project/name %) "simple")(get-all-projects)))







(defn tasks-for-project [project-id]
  (let [url (str "https://flu.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('" project-id "')/Tasks")
        response (-> (client/get url
                                 (project/prepare-request-options))
                     :body
                     (json/read-str :key-fn keyword)
                     :value)

        all-tasks (map #(select-keys % [:Name :Id]) response)
        all-tasks-renamed (mapv #(s/rename-keys % {:Name :task/name :Id :task/id}) all-tasks)]
    all-tasks-renamed))









(defn transact-all2
  ([conn txs]
     (transact-all2 conn txs nil))
  ([conn txs res]
     (if (seq txs)
       (transact-all2 conn (rest txs) @(d/transact conn (first txs)))
       res)))




                                        ;(transact-all  conn "resources/edn/schema.edn")
                                        ;(transact-all2 conn (reduce (fn [r x] (conj r [x] )) [] assignement-phased))
(def all-projects (butlast (get-all-projects)))

(defn project-by-name
  [name]
  (filter #(= (:project/name %) name) (get-all-projects)))

#_(defn assignements-for-project
    [name]
    (filter #(= (:project/name %)  name) assignement-phased))



#_(def my-project-data
    (let [project (first (project-by-name "simple"))
          assignments (assignements-for-project "simple")
          ]
      (assoc project :project/assignments assignments )))



#_(d/q '[:find ?e
         :where
         [?e :project/id #uuid "df2ebab7-0816-ea11-a08e-005056eb471c"]
         [?e :task/name "model domain"]
         [?e :project/assignments ?a ]]
       db)


;; To repeat

#_(def conn (scratch-conn))
#_(transact-all  conn2 "resources/edn/schema.edn")




#_(def simple-project (filter #(= (:project/name %) "simple") all-projects))


#_(d/q '[:find ?day ?a ?pn ?n ?w
       :keys day id project task work
       :where
       [?a :assignment/by-day ?day]
       [(.after ?day #inst "2019-03-08T00:00:00.000-00:00")]
       [(.before ?day #inst "2019-04-01T00:00:00.000-00:00")]
       [?a :assignment/task ?t]
       [?t :task/name ?n]
       [?pr :project/assignments ?a]
       [?pr :project/name ?pn]
       [?a :assignment/resource ?r]
       [?r :resource/name "Newsha Neishaboory"]
       [?a :assignment/work ?w]
       ] (d/db conn2))




#_(d/q '
 [:find ?day ?task-name ?work ?project
  :where
  [?resource :resource/name "Newsha Neishaboory"]
  [?assignment :assignment/by-day ?day]
  [?assignment :assignment/task ?task]
  [?task :task/name ?task-name]
  [(.after ?day #inst "2019-03-08T00:00:00.000-00:00")]
  [(.before ?day #inst "2019-03-10T00:00:00.000-00:00")]
  [?p :project/name ?project]
  [?assignment :assignment/work ?work]
  ])

#_(def r (reduce (fn [s a]
                 (if-not (contains? (set (map :assignment/id s))
                                    (:assignment/id a))
                   (conj s a))
                 s) #{} (:project/assignments one-pr)))


(defn find-in-grouped-by [m key]
  (->> (seq m)
       (reduce (fn [r [{:keys [:user-id :a]} :as k] v] (update r a conj {k v}))) {}))

(def r (take 5 (d/q '[:find ?a ?pn ?name ?bd
               :keys :db/id project task day
               :where
               [?a :assignment/task ?t]
               [?t :task/name ?name]
               [?a :assignment/by-day ?bd]
               [?p :project/name ?pn]
               [?a :assignment/resource ?resource]
               [?resource :resource/email-address "nneishaboory@fluxym.com"]
               ] (d/db conn2))))
