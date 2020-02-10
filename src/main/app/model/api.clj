(ns app.model.api
  (:require ;[com.wsscode.pathom.connect :as pc]
                                        ; [taoensso.timbre :as log]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [app.model.project-model :as project]
                                        ;[app.server-components.pathom :as pathom]
   [clojure.set :as s]
   [app.model.database :refer [db-url]]
   
   [overtone.at-at :as at-at]
   [tick.alpha.api :as t]
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
                                        ;[app.model.database :as db :refer [conn]]
                                        ;[clj-time.core :as t]
   [tick.alpha.api :as t]
   
                                        ;[app.model.project :as p]
   [clojure.core :refer :all])
  (:import datomic.Util))


                                        ;(def conn2 (d/connect "datomic:dev://localhost:4334/one2"))




(def project-keys [:project/id
                   :project/finish-date
                   #_:project/author-name  :project/start-date
                   :project/name
                   :project/work
                   :project/created-date
                   :project/modified-date
                   :project/last-published-date
                   ])

                                        ;(def db-url "datomic:dev://localhost:4334/one2")

                                        ;(d/create-database "datomic:dev://localhost:4334/one2")
                                        ;(d/delete-database db-url)

                                        ;(def conn2 (d/connect db-url))

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






(defn uuid [s]
  (if (string? s)
    (java.util.UUID/fromString (.toLowerCase s))
    s))



(def assignment-phased-keys
  '(:task/name
    
    :task/is-active

    :task/end-date
    :task/start-date

    :parent/task-id
    :parent/task-name

    :task/is-root?
    
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

    (pmap #(select-keys % resource-keys) response)
    (pmap
     (fn [x] (update x :resource/created-date #(when-not (nil? %)
                                                 (clojure.instant/read-instant-date %))))
     response)

    (pmap
     (fn [x] (update x :resource/modified-date #(when-not (nil? %)
                                                  (clojure.instant/read-instant-date %))))
     response)


    (pmap (fn [x] (update x :resource/id #(uuid %))) response)
    
    ;; (pmap (fn [x] (assoc x
    ;;                ;:resource/active? true
    ;;                     ;:resource/profile :profile/user
    ;;                     )) response)
    
    
    ))

(defn remove-nils
  [m]
  (apply dissoc                                                                                            
         m                                                                                                  
         (for [[k v] m :when (nil? v)] k)))

(defn get-resource
  [id]
  (let [resource-server (first (filter #(= (:resource/id %)  id) resources))
        val  (d/entity (d/db (d/connect db-url)) [:resource/id  id])]
    
    (if (number? (:db/id id)) id (remove-nils resource-server))))


(defn assignement-phased-project-id
  [id skip]
  (let [max 2000
        url (str "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/AssignmentTimephasedDataSet?$filter=ProjectId eq guid'" id "'&$skip=" (str skip) "&$top=" (str max))]
    (as-> (-> (client/get url
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

      

      (pmap #(select-keys % assignment-phased-keys) response)

      

      (pmap
       (fn [x] (update x :assignment/work #(when-not (nil? %)
                                             (clojure.edn/read-string %))))
       response)

      (pmap
       (fn [x] (update x :time/by-day  #(when-not (nil? %)
                                          (clojure.instant/read-instant-date %))))
       response)

      (pmap
       (fn [x] (update x :assignment/modified-date
                       #(when-not (nil? %)
                          (clojure.instant/read-instant-date %))))
       response)

      (pmap
       (fn [x] (update x :task/start-date
                       #(when-not (nil? %)
                          (clojure.instant/read-instant-date %))))
       response

       )

      (pmap
       (fn [x] (update x :task/end-date
                       #(when-not (nil? %)
                          (clojure.instant/read-instant-date %))))
       response

       )

      (pmap #(s/rename-keys % {:time/by-day :assignment/by-day}) response)

      

      (pmap (fn [x] (update x :task/id #(uuid %))) response)
      (pmap (fn [x] (update x :project/id #(uuid %))) response)
      (pmap (fn [x] (update x :assignment/id #(uuid %))) response)
      (pmap (fn [x] (update x :resource/id #(uuid %))) response)

      
      (pmap (fn [x] (let [task-name (:task/name x)
                         task-id (:task/id x)
                         task-is-active (:task/is-active x )
                         task-start-date (:task/start-date x)

                         task-end-date (:task/end-date x)
                         task-is-root (= (:parent/task-id x) (:task/id x))
                         parent-task-id (uuid (:parent/task-id x))

                         parent-task-name (:parent/task-name x)

                         task-project [:project/id (uuid (:project/id x))]


                         
                         resource (get-resource (:resource/id x))
                         
                         
                         ]
                     
                     
                     (remove-nils (assoc (dissoc x :task/name :task/id :task/is-active :project/id  :resource/id :task/start-date :task/end-date)
                                    :assignment/task {:task/name task-name :task/id (uuid task-id) }
                                    :assignment/resource resource)))) response)
      
      response)))

(defn all-assignments 
  [id]
  (loop [r []
         skip 0]
    (let [one-r (assignement-phased-project-id id skip)]
      (if (seq one-r)
        (do
          
          (recur (concat r one-r) (+ skip 2000)))
        r))))



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

(def task-props [:task/name :task/is-active :task/is-root?  :task/parent-task-name :task/parent-task-id :task/is-root? :task/start-date :task/end-date :task/id])




(defn tasks-for-project
  [id]
  (as-> (-> (client/get
             (str "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/Projects(guid'"  (str id)  "')/Tasks")
             
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

    (pmap
     (fn [x] (update x :task/start-date
                     #(when-not (nil? %)
                        (clojure.instant/read-instant-date %))))
     response

     )

    (pmap
     (fn [x] (update x :task/finish-date
                     #(when-not (nil? %)
                        (clojure.instant/read-instant-date %))))
     response

     )

    

    (pmap (fn [x] (update x :task/id #(uuid %))) response)

    (pmap (fn [x]
           (let [task-name (:task/name x)
                 task-id (:task/id x)
                 task-is-active (:task/is-active x )
                 task-start-date (:task/start-date x)
                 task-outline-number (:task/outline-number x)
                 task-outline-level (:task/outline-level x)
                 task-end-date (:task/finish-date x)
                 task-is-root (= (str (:parent/task-id x)) (str (:task/id x)))
                                        ;parent-task (if (= (str (:parent/task-id x)) (str (:task/id x))) nil [:task/id (uuid (:parent/task-id x))])

                 parent-task-name (:parent/task-name x)
                 parent-task-id (uuid (:parent/task-id x))
                 
                 task-project [:project/id (uuid (:project/id x))]
                 ]


                                        ;(println "ID 2 " )
             (remove-nils
              (hash-map
               
               :task/name task-name
               :task/id  task-id
               
               :task/is-active task-is-active
               :task/outline-level task-outline-level
               :task/outline-number task-outline-number
               
                                        ;:db/id (or id "new")
               :task/parent-task-id parent-task-id
               :task/parent-task-name parent-task-name
               
                                        ;:task/parent-task-name parent-task-name
                                        ;:task/project task-project
               :task/is-root? task-is-root
               :task/start-date task-start-date
               :task/end-date task-end-date))))
         response)

                                        ;(pmap #(select-keys % task-props) response)
    response))


                                        ;tasks-for-project



(defn get-all-projects
  []
  (let [response (-> (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectData/%5Ben-us%5D/Projects"
                                 (project/prepare-request-options))
                     :body
                     (json/read-str :key-fn keyword)
                     :value)]
    (->> response
         ;(pmap #(select-keys % [:Name :Id]) )
         (pmap #(s/rename-keys % {:Name :project/name :Id :project/id}) )
         (cske/transform-keys csk/->kebab-case-keyword )
         (cske/transform-keys
          (fn [x]
            (keyword (clojure.string/replace-first (name x) #"-" "/")) )             )
         (pmap #(select-keys % project-keys) )
         (pmap (fn [x] (update x :project/id #(uuid %))))
         (pmap
          (fn [x] (update x :project/start-date #(when-not (nil? %)
                                                   (clojure.instant/read-instant-date %)))))
         (pmap
          (fn [x] (update x :project/finish-date #(when-not (nil? %)
                                                    (clojure.instant/read-instant-date %)))))
         (pmap
          (fn [x] (update x :project/work #(when-not (nil? %)
                                             (clojure.edn/read-string %)))))
         (pmap
          (fn [x] (update x :project/created-date #(when-not (nil? %)

                                                     (clojure.instant/read-instant-date %))))
          )
         
         (pmap
          (fn [x] (update x :project/modified-date #(when-not (nil? %)

                                                      (clojure.instant/read-instant-date %)))))
         (pmap
          (fn [x] (update x :project/last-published-date #(when-not (nil? %)

                                                            (clojure.instant/read-instant-date %))))
          )
         (pmap remove-nils)
         (pmap
          (fn [x] (remove-nils (assoc x
                                 :project/assignments (pmap (fn [x] (dissoc x :project/name))  (all-assignments (str (:project/id x))))
                                 :project/tasks (tasks-for-project (str (:project/id x))))))
            
            )
         ;(pmap (fn [x] (:assoc)))
         )))




(defn transact-all2
  ([conn txs]
     (transact-all2 conn txs nil))
  ([conn txs res]
     (if (seq txs)
       (transact-all2 conn (rest txs) @(d/transact conn (first txs)))
       res)))

                                        ;(def conn (scratch-conn))
                                        ;(transact-all  conn2 "resources/edn/schema.edn")

(defn find-in-grouped-by [m key]
  (->> (seq m)
       (reduce (fn [r [{:keys [:user-id :a]} :as k] v] (update r a conj {k v}))) {}))



;; (d/transact {:db/id 17592186048985 :resource/profile}
;;             (d/connect ))

#_(d/transact (d/connect "datomic:dev://localhost:4334/one2") (get-all-projects))

                                        ;(println "Test")



;; TODO transact per project
(defn update-db []
  (println "delete projects...........")
  (d/transact
   (d/connect db-url)

   (pmap
    (fn [id]

      [:db/retractEntity id])

    (d/q '[:find [?e ...]

           :where
           [?e :project/name ?n]

           ] (d/db (d/connect db-url)))))

  (println "seed projects...............")
  @(d/transact (d/connect db-url)  (get-all-projects))

  (println "Done....................")
                                        ;(at-at/show-schedule my-pool)
                                        ;  (user/restart)
  )


(defn first-time-db []
  (d/create-database db-url)
  (println "seeding schema")
  
  (transact-all (d/connect db-url) "resources/edn/schema.edn")
  (update-db))





(def my-pool (at-at/mk-pool))

(def schedule (at-at/every (t/millis (t/new-duration 250 :minutes)) update-db my-pool :initial-delay (t/millis (t/new-duration 400 :minutes))))
                                        ;(at-at/stop schedule)
                                        ;
                                        ;(transact-all (d/connect "datomic:dev://localhost:4334/one2") "resources/edn/schema.edn")









(comment
  (d/transact connection [{:action-list/id #uuid "850d9f1e-27e1-e911-b19b-9cb6d0e1bd60"
                           :action-list/actions [{:action/action "Action 1"
                                                  :db/id "new"
                                                  :action/owner "Sifou"
                                                  :action/status :open
                                                  :action/due-date (t/inst (t/now))}
                                                 {:db/id "new2"
                                                  :action/action "Action 2"
                                                  :action/owner "Sifou"
                                                  :action/status :closed
                                                  :action/due-date (t/inst (t/now))}]}])
  
  (pmap (comp (fn [m] (update m :action-list/actions (fn [x] (pmap #(clojure.set/rename-keys %  {:db/id :action/id}) x)))) first)
        )


  (first )
  (d/transact connection [[:db/retractEntity 17592186087466]])



  (d/transact connection [[:db/retractEntity 17592186087495] [:db/retractEntity 17592186087496] [:db/retractEntity 17592186087487] [:db/retractEntity 17592186087488]])

  )


