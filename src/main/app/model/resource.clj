
(ns app.model.resource
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [app.model.api :as api]
   [clojure.set :as set]
   [datomic.api :as d]
   [app.model.database :refer [db-url]]))




(pc/defresolver resource-resolver [{:keys [db]} {:resource/keys [id]}]
  {::pc/input  #{:resource/id}
   ::pc/output [:resource/name  :resource/id :resource/email-address]}
  (when id
    (first (d/q '[:find ?name ?ea  ?active
                  :in $ ?id
                  :keys resource/name resource/email-address  resource/active?
                  :where
                  [?r :resource/id ?id]
                  [?r :resource/name ?name]
                  [?r :resource/email-address ?ea]
                  [?r :resource/active? ?active]

                  
                  ] db (api/uuid id)))))






(pc/defresolver resource-profile [{:keys [db]} {:resource/keys [id]}]
  {::pc/input  #{:resource/id}
   ::pc/output [:resource/profile]}
  (when id
    {:resource/profile
     (or (d/q '[:find ?profile .
                :in $ ?id
                
                :where
                [?r :resource/id ?id]
                [?r :resource/profile ?profile]
                

                
                ] db id)
         :profile/user)}))



(pc/defmutation set-resource-profile [{:keys [db connection]} {:keys [id value]}]
  {::pc/sym `set-resource-profile
   ::pc/params [:id :val]
   ::pc/output [:resource/id]}
  (d/transact connection
              [{:db/id [:resource/id id] :resource/profile value}])
  
  {:resource/id id})



(pc/defmutation set-resource-active? [{:keys [db connection]} {:keys [id value]}]
  {::pc/sym `set-resource-active?
   ::pc/params [:id :val]
   ::pc/output [:resource/id]}
  (d/transact connection
              [{:db/id [:resource/id id] :resource/active? value}])
  
  {:resource/id id})


(pc/defmutation set-resource-actuals? [{:keys [db connection]} {:keys [id value]}]
  {::pc/sym `set-resource-actuals?
   ::pc/params [:id :val]
   ::pc/output [:resource/id]}
  (d/transact connection
              [{:db/id [:resource/id id] :resource/allow-actuals? value}])
  
  {:resource/id id})


(pc/defmutation set-resource-forecast? [{:keys [db connection]} {:keys [id value]}]
  {::pc/sym `set-resource-forecast?
   ::pc/params [:id :val]
   ::pc/output [:resource/id]}

    
  (d/transact connection
              [{:db/id [:resource/id id] :resource/allow-forecast? value}])
  
  {:resource/id id})


(pc/defresolver all-resources-resolver [{:keys [db connection]} _]
  {::pc/output [{:resource/all-resources [:resource/id :resource/name :resource/email-address]}]}
  {:resource/all-resources (map #(d/touch (d/entity  db [:resource/id (:resource/id %)]))
                                (flatten (seq (d/q  '[:find ?f
                                                      :keys resource/id
                                                      :where
                                                      [?e :resource/id ?f]
                                                      ] db))))})

(def resolvers  [resource-profile set-resource-profile resource-resolver all-resources-resolver set-resource-active? set-resource-actuals? set-resource-forecast?])
