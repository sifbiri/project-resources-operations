(ns app.model.resource
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   
   [datomic.api :as d]))




(pc/defresolver resource-resolver [{:keys [db]} {:resource/keys [id]}]
  {::pc/input  #{:resource/id}
   ::pc/output [:resource/name :resource/email-address]}
  (first (d/q '[:find ?name ?ea ?profile ?active
                :in $ ?id
                :keys :resource/name :resource/email-address :resource/profile :resource/active?
                :where
                [?r :resource/id ?id]
                [?r :resource/name ?name]
                [?r :resource/email-address ?ea]

                [?r :resource/profile ?profile]
                [?r :resource/active? ?active]

                
                ] (d/db (d/connect "datomic:dev://localhost:4334/one2")) id)))



(pc/defmutation set-resource-profile [{:keys [db connection]} {:keys [id value]}]
  {::pc/sym `set-resource-profile
   ::pc/params [:id :val]
   ::pc/output [:resource/id]}
  (d/transact (d/connect "datomic:dev://localhost:4334/one2")
              [{:db/id [:resource/id id] :resource/profile value}])
  
  {:resource/id id})


(pc/defresolver all-resources-resolver [{:keys [db connection]} _]
  {::pc/output [{:resource/all-resources [:resource/id :resource/name :resource/email-address]}]}
  {:resource/all-resources (map #(d/touch (d/entity  (d/db (d/connect "datomic:dev://localhost:4334/one2")) [:resource/id (:resource/id %)]))
                                (flatten (seq (d/q  '[:find ?f
                                                      :keys resource/id
                                                      :where
                                                      [?e :resource/id ?f]
                                                      ] db))))})

(def resolvers  [set-resource-profile resource-resolver all-resources-resolver ])
