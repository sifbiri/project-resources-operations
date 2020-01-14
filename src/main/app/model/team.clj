(ns app.model.team
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   
   [datomic.api :as d]))


(pc/defresolver team-resolver [env {:team/keys [name]}]
  {::pc/input  #{:team/name}
   ::pc/output [:team/name :team/type :db/id
                {:team/lead [:resource/name :resource/id :resource/email-address]}
                {:team/resources [:resource/name :resource/id :resource/email-address]} ]}
  
  (d/pull (d/db (d/connect "datomic:dev://localhost:4334/one2")) [:db/id :team/name :team/type {:team/resources [:resource/name :resource/email-address :resource/id]} {:team/lead [:resource/name :resource/email-address :resource/id]} ] [:team/name name]))


(pc/defresolver all-teams-resolver [{:keys [db connection]} _]
  {::pc/output [{:teams [:team/name]}]}
  {:teams (d/q  '[:find ?name
                  :keys team/name
                  :where
                  [?e :team/name ?name]
                  ] (d/db (d/connect "datomic:dev://localhost:4334/one2")))})



(def resolvers  [team-resolver all-teams-resolver])
