(ns app.model.project
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   
   [datomic.api :as d]))

(def db-url "datomic:dev://localhost:4334/one2")


(def conn2 (d/connect db-url))

(pc/defresolver resource-resolver [env {:resource/keys [id]}]
  {::pc/input  #{:resource/id}
   ::pc/output [:resource/name :resource/email-address]}
  (first (d/q '[:find ?name ?ea
                :in $ ?id
                :keys :resource/name :resource/email-address
                :where
                [?r :resource/id ?id]
                [?r :resource/name ?name]
                [?r :resource/email-address ?ea]
                ] (d/db conn) id)
         ))

(pc/defresolver projects-resolver [env _]
  {::pc/output [{:projects [:project/id :project/name]}]}
  (let [id (get-in env [:ast :params :resource/id])]
    {:projects (map (fn [row]
                      (zipmap [:project/name :project/id] row))
                    (d/q  '[:find ?pn ?pi
                            :in $ ?id
                            
                            :where
                            [?p :project/name ?pn]
                            [?p :project/id ?pi]
                            [?p :project/assignments ?a]
                            [?a :assignment/resource ?r]
                            [?r :resource/id ?id]] (d/db conn) id))}))

(pc/defresolver assignment-resolver [evn {:assignment/keys [id]}]
  {::pc/input #{:assignment/id}
   ::pc/output [:assignement/id :assignment/day :assignement/name :assignment/work]}
  (d/q '[:find ?name ?id ?bd ?w
         :keys :assignment/name :assignment/id :assignment/day :assignment/work
         :where
         :in $ ?id
         [?pr :project/assignments 17592186051853]
         [?id :assignment/by-day ?bd]
         [?id :assignment/work ?w]
         [?id :assignment/task ?t]
         [?t :task/name ?name]
         [?id :assignment/id ?id]
         ] (d/db conn) id))

(pc/defresolver assignments-resolver [env _]
  {::pc/output [{:assignments [:assignment/id]}]}
  (let [resource-id (get-in env [:ast :params :resource/id])
        project-id (get-in env [:ast :params :project/id])]
    {:assignments (map (fn [row] (zipmap [:assignment/id :assignment/name :assignment/day :assignment/work] row))
                       (seq (d/q '[:find ?a ?tn ?bd ?w
                                   :in $ ?ri ?pid
                                   :where
                                   [?p :project/id ?pid]
                                   [?r :resource/id ?ri]
                                   [?a :assignment/resource ?r]
                                   [?a :assignment/work ?w]
                                   [?p :project/assignments ?a]
                                   [?a :assignment/task ?t]
                                   [?t :task/name ?tn]
                                   [?a :assignment/by-day ?bd]
                                   ] (d/db conn2) resource-id project-id)))}))

(def resolvers  [projects-resolver assignments-resolver assignment-resolver resource-resolver])




