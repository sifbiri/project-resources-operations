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





(pc/defmutation set-team-name [{:keys [db connection]} {:keys [team-id name]}]
  {::pc/sym`set-team-name
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (let [ ]
    (d/transact connection [{:db/id team-id
                             :team/name name}])
    {:team/name name}))



(pc/defmutation set-team-type [{:keys [db connection]} {:keys [team-id type]}]
  {::pc/sym`set-team-type
   ::pc/params [:team-id :team-member-id]
   ::pc/output []}
  (let [ ]
    (d/transact connection [{:db/id team-id
                             :team/type type}])
    {}))





(pc/defmutation set-team-lead [{:keys [db connection]} {:keys [team-id lead-id]}]
  {::pc/sym`set-team-lead
   ::pc/params [:team-id :team-member-id]
   ::pc/output []}
  (let [ ]
    (d/transact connection [{:db/id team-id
                             :team/lead [:resource/id lead-id]}])
    {}))


#_(defmutation set-team-type [{:keys [team-id type]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:team/id team-id :team/type] type)))))
  (remote [env] true))


(pc/defmutation add-team-member [{:keys [db connection]} {:keys [team-id team-member-id]}]
  {::pc/sym`add-team-member
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (let [{:keys [team/resources team/name]} (d/pull db [:team/resources :team/name] 17592186109848)
        resources-added (conj resources [:resource/id team-member-id])]
    (d/transact connection [{:db/id team-id
                             :team/resources resources-added}])
    {:team/name name}))



(pc/defmutation delete-team-member [{:keys [db connection]} {:keys [team-id team-member-id]}]
  {::pc/sym`delete-team-member
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (let [{:keys [team/resources team/name]} (d/pull db [:team/resources :team/name] 17592186109848)

        resources2 (map #(:db/id %) resources)
        resource-to-remove (d/q '[:find ?e . :in $ ?id :where [?e :resource/id ?id]] db team-member-id)
        ]
    (d/transact connection [[:db/retract team-id :team/resources resource-to-remove]])
    {:team/name name}))



(pc/defresolver all-teams-resolver [{:keys [db connection]} _]
  {::pc/output [{:teams [:team/name]}]}
  {:teams (d/q  '[:find ?name
                  :keys team/name
                  :where
                  [?e :team/name ?name]
                  ] (d/db (d/connect "datomic:dev://localhost:4334/one2")))})



(def resolvers  [team-resolver all-teams-resolver add-team-member delete-team-member set-team-name set-team-type set-team-lead])
