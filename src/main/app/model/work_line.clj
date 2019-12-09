(ns app.model.work-line
  (:require [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            [app.model.project-model :as project]
            [clojure.set :as s]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.json :as json]
                                        ;[datomic.client.api :as d]
            [datomic.api :as d]
            [clj-http.client :as client]
            ))

(def work-lines (atom {;; 14
                       ;; {:work-line/id 14
                       ;;  :work-line/project {:project/id "ee40bf0b-6af6-e911-b19c-9cb6d0e1bd60"}
                       ;;  :work-line/task {:task/id "3941bf0b-6af6-e911-b19c-9cb6d0e1bd60"}
                       ;;  :work-line/hours 22}
                       13
                       {:work-line/id 13
                        :work-line/project {:project/id "ee40bf0b-6af6-e911-b19c-9cb6d0e1bd60"}
                        :work-line/task {:task/id "3941bf0b-6af6-e911-b19c-9cb6d0e1bd60" }
                        :work-line/hours 22}}))

;(require '[datomic.api :as d])
(def uri "datomic:dev://localhost:4334/one")
(d/create-database uri)
(def conn (d/connect uri))
(def db (d/db conn))

;(require '[datomic.api :as d])
;; (def uri "datomic:free://localhost:4334/test")
;; (def conn (da/connect uri))
;; (def db (da/db conn))
;(set! *print-length* 100)


;(def  uri "datomic:free://localhost:4334/mbrainz-1968-1973")

;(da/create-database uri)


(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :validate-hostnames false
          :endpoint "localhost:8998"})





;; (def client (d/client cfg))

;; ;(d/delete-database client {:db-name "hello"})

;; (def conn (d/connect client {:db-name "fp"}))

(defn uuid [] (str (java.util.UUID/randomUUID)))




(def sample-data [{:work-line/id #uuid "f9798479-87b3-4918-9d62-6cb507de13f3"
                   :work-line/project {:project/id #uuid "ee40bf0b-6af6-e911-b19c-9cb6d0e1bd60"}
                   :work-line/task {:task/id #uuid "3941bf0b-6af6-e911-b19c-9cb6d0e1bd60" }
                   :work-line/hour 22}])

#_(d/transact conn {:tx-data sample-data})







(def projects (atom {1 {:project/id   1
                        :project/name "Fluxod"}

                     2 {:project/id   2
                        :project/name "OLB"}}))

(defn next-id []
  (inc (reduce max (-> work-lines deref keys))))

(pc/defmutation save-work-line [env {:work-line/keys [id]
                                     :keys      [diff]}]
  {::pc/output [:work-line/id]}
                                        ;(throw (ex-info "Boo" {}))


  
;(println "difff" diff)
  (let [new-values (get diff [:work-line/id id])
        new?       (not (contains? (set (keys @work-lines)) id))
        real-id     id
        [_ project-id] (get new-values :work-line/project)
        [_ task-id] (get new-values :work-line/task)
        new-values (cond-> new-values
                     new? (assoc :work-line/id real-id)
                     project-id (assoc :work-line/project {:project/id project-id})
                     task-id (assoc :work-line/task {:task/id task-id}))]
    
    ;(log/info "Saving " new-values " for item " id)
                                        ;(Thread/sleep 500)
    (println "ssssssssssssssss")
    
    (println "new?" new?)
    (println "ID is " id)
    (if new?
      (swap! work-lines assoc real-id new-values)
      (swap! work-lines update real-id merge new-values))
    {:work-line/id real-id}))
    

;; TODO duplicated in api
(defn get-all-projects
  []
  (let [response (-> (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectServer/Projects"
                                 (project/prepare-request-options))
                     :body
                     (json/read-str :key-fn keyword)
                     :value)
        all-projects (map #(select-keys % [:Name :Id]) response)
        all-projects-renamed (mapv #(s/rename-keys % {:Name :project/name :Id :project/id}) all-projects)]

    all-projects-renamed))



                                        ;(def project-id "1747f6cc-bdfc-e911-b19d-9cb6d0e1bd60")


;; TODO duplicated 
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

(pc/defresolver all-tasks-for-project-resolver [env {:project/keys [id]}]
  {::pc/output [{:work-line/tasks [:task/id :task/name]}]}
  (let [{:keys [project-id]} (-> env :ast :params)]
                                        ;(println "server " project-id)
    {:work-line/tasks (tasks-for-project project-id)}))

(pc/defresolver all-projects-resolver [_ _]
  {::pc/output [{:work-line/all-projects [:project/id]}]}
  {:work-line/all-projects (get-all-projects)})

(pc/defresolver project-resolver [env {:project/keys [id]}]
  {::pc/input  #{:project/id}
   ::pc/output [:project/name]}
  (get @projects id))

#_(pc/defresolver task-resolver [env {:task/keys [id]}]

  {::pc/input #{:task/id}
   ::pc/output [:task/name]}
  {:task/name "Test"
   :task/id id})

(pc/defresolver work-line-resolver [env {:work-line/keys [id]}]
  {::pc/input  #{:work-line/id}
   ::pc/output [:work-line/id :work-line/hours  {:work-line/project [:project/id]} {:work-line/task [:project/id]}]}
  (get @work-lines id))


;(def db (d/db conn))
;; a comment


;(def res1 (vector (d/pull (d/db conn) [:work-line/id :work-line/hour {:work-line/project [:project/id]} {:work-line/task [:task/id]}] [:work-line/id  "0b249fcf-a55e-49b9-9c74-6cb7cbccc912"])))

;(def res2 (-> @work-lines vals vec))

(pc/defresolver all-work-lines-resolver [{:keys [db connection] :as env} stuff]
  {::pc/output [{:work-day/all-work-lines [:work-line/id :work-line/hour
                                           :work-line/project
                                           :work-line/assignment]}]}
  (let [by-day (-> env :ast :params :by-day)
        username (-> env :ast :params :username)

        q (d/q '[:find ?a ?pn ?n  ?w
                 :in $ ?by-day ?username
                 
                 :where
                 [?a :assignment/by-day ?by-day]
                 [?a :assignment/task ?t]
                 [?t :task/name ?n]
                 [?pr :project/assignments ?a]
                 [?pr :project/name ?pn]
                 [?a :assignment/resource ?r]
                 [?r :resource/email-address ?username]
                 [?a :assignment/work ?w]
                 ] (d/db conn) by-day username)
        mapped (map (fn [x](zipmap [:work-line/id :work-line/project :work-line/assignment :work-line/hour] x) ) q)]

    
    (println "date is " by-day)
    {:work-day/all-work-lines mapped}))



#_(pc/defmutation save-item [env {:item/keys [id]
                                  :keys      [diff]}]
    {::pc/output [:item/id]}
                                        ;(throw (ex-info "Boo" {}))
    (println "h")
    (let [new-values (get diff [:item/id id])
          new?       (tempid/tempid? id)
          real-id    (if new? (next-id) id)
          [_ category-id] (get new-values :item/category)
          new-values (cond-> new-values
                       new? (assoc :item/id real-id)
                       category-id (assoc :item/category {:category/id category-id}))]
      (log/info "Saving " new-values " for item " id)
      (Thread/sleep 500)
      (if new?
        (swap! items assoc real-id new-values)
        (swap! items update real-id merge new-values))
      (cond-> {:item/id real-id}
        new? (assoc :tempids {id real-id}))))

(def resolvers [work-line-resolver all-work-lines-resolver all-projects-resolver project-resolver
                all-tasks-for-project-resolver save-work-line #_task-resolver])



#_(def schema2
  [{:db/doc "A users email."
    :db/ident :user/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(def schema2
  [{:db/doc "A users email."
    :db/ident :user/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/doc "A users age."
    :db/ident :user/age
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}


   {:db/doc "A users age."
    :db/ident :car/make
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/doc "A users age."
    :db/ident :car/model
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}


   {:db/doc "A users age."
    :db/ident :year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/doc "A users age."
    :db/ident :user/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}


   {:db/doc "List of cars a user owns"
    :db/ident :cars
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}])

(def testdata [{:user/email "sbiri@fluxym.com"}])


(def test-data
  [{:db/id "taco"
    :car/make "toyota"
    :car/model "tacoma"
    :year 2014}

   {:db/id "325"
    :car/make "BMW"
    :car/model "325xi"
    :year 2001}

   {:db/id 3
    :user/name "ftravers"
    :user/age 54
    :cars [{:db/id "taco"}
           {:db/id "325"}]}])


(def test-data-fp
  [{:work-line/id  "0b249fcf-a55e-49b9-9c74-6cb7cbccc912"
    :db/id "work-line"
    :work-line/project  {:db/id "project"}
    :work-line/task  {:db/id "task"}
    :work-line/hour 22}

   {:db/id "project"
    :project/id  "ee40bf0b-6af6-e911-b19c-9cb6d0e1bd60"}

   {:db/id "task"
    :task/id  "3941bf0b-6af6-e911-b19c-9cb6d0e1bd60" }])


