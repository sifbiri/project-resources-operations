(ns app.model.project
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.core.async :refer [go]]
   [clojure.set :as set]
   [app.model.api :as api]
   [tick.alpha.api :as t]
   
   [datomic.api :as d]))


(def gov-review-week-keys  [:gov-review-week/week
                            :gov-review-week/status
                            :gov-review-week/exec-summary-text
                            :gov-review-week/exec-summary-color
                            {:gov-review-week/project
                             [:project/id
                              :project/name
                              :project/start-date
                              :project/last-published-date
                              :project/modified-date
                              :project/finish-date]}
                            :gov-review-week/client-relationship-text
                            :gov-review-week/client-relationship-color
                            :gov-review-week/finance-text
                            :gov-review-week/finance-color
                            :gov-review-week/scope-schedule-text
                            :gov-review-week/scope-schedule-color
                            {:gov-review-week/submitted-by
                             [:resource/id
                              :resource/name
                              :resource/email-address
                              :resource/active?
                              :resource/profile]} 
                            :gov-review-week/submitted-at])

(defn round-to-first-day-of-week [ts]
  (loop [ts ts
         day (t/day-of-week ts)]
    (if (not= day t/MONDAY)
      (recur (t/- ts (t/new-period 1 :days))
             (t/day-of-week (t/- ts (t/new-period 1 :days)))
             
             
             )
      ts)))



(def db-url "datomic:dev://localhost:4334/one2")


(def conn2 (d/connect db-url))
(def alias-project-info-project-panel (pc/alias-resolver2 :project-panel/id :project-info/id))

(pc/defresolver resource-resolver [{:keys [db]} {:resource/keys [id]}]
  {::pc/input  #{:resource/id}
   ::pc/output [:resource/name :resource/email-address]}
  (first (d/q '[:find ?name ?ea
                :in $ ?id
                :keys resource/name resource/email-address
                :where
                [?r :resource/id ?id]
                [?r :resource/name ?name]
                [?r :resource/email-address ?ea]
                ] db (api/uuid id))
         ))


;; (pc/defresolver overall-exec-summary-color-resolver  [{:keys [db]} {:keys [project/id]}]
;;     {::pc/input  #{:project/id}
;;      ::pc/output [:gov-review-week/exec-summary-color]}
;;     (select-keys (last (sort-by :gov-review-week/week
;;                                 (map first (d/q '[:find (pull ?gw [:gov-review-week/exec-summary-color :gov-review-week/week])
;;                                                   :in $ ?id
                                                  
;;                                                   :where
;;                                                   [?p :project/id ?id]
;;                                                   [?gw :gov-review-week/project ?p]
                                                  
;;                                                   ] db id )
;;                                      ))) [:gov-review-week/exec-summary-color]))


;; (pc/defresolver overall-exec-summary-color-resolver  [{:keys [db]} in]
;;   {::pc/input #{:project/id}
;;    ::pc/output  [:gov-review-week/exec-summary-color]
;;    ::pc/transform pc/transform-batch-resolver}
;;   (mapv (fn [{:keys [project/id]}]
;;           {:gov-review-week/exec-summary-color :green}) in))




(pc/defresolver overall-exec-summary-color-resolver  [{:keys [db]} {:project/keys [id]}]  
  {::pc/input  #{:project/id}
   ::pc/output [:gov-review-week/exec-summary-color :project/id]}
  (assoc (last (sort-by :gov-review-week/week
                        (map first (d/q '[:find (pull ?gw [:gov-review-week/exec-summary-color :gov-review-week/week])
                                          :in $ ?id
                                          
                                          :where
                                          [?p :project/id ?id]
                                          [?gw :gov-review-week/project ?p]
                                          [?gw :gov-review-week/status :submitted]
                                          
                                          ] db  id)
                             )))
    :project/id id))



(pc/defresolver client-relationship-color-resolver  [{:keys [db]} {:project/keys [id]}]  
  {::pc/input  #{:project/id}
   ::pc/output [:gov-review-week/client-relationship-color :project/id]}
  (assoc (last (sort-by :gov-review-week/week
                        (map first (d/q '[:find (pull ?gw [:gov-review-week/client-relationship-color :gov-review-week/week])
                                          :in $ ?id
                                          
                                          :where
                                          [?p :project/id ?id]
                                          [?gw :gov-review-week/project ?p]
                                          [?gw :gov-review-week/status :submitted]
                                          
                                          ] db  id)
                             )))
    :project/id id))


(pc/defresolver scope-schedule-color-resolver  [{:keys [db]} {:project/keys [id]}]  
  {::pc/input  #{:project/id}
   ::pc/output [:gov-review-week/scope-schedule-color :project/id]}
  (assoc (last (sort-by :gov-review-week/week
                        (map first (d/q '[:find (pull ?gw [:gov-review-week/scope-schedule-color :gov-review-week/week])
                                          :in $ ?id
                                          
                                          :where
                                          [?p :project/id ?id]
                                          [?gw :gov-review-week/project ?p]
                                          [?gw :gov-review-week/status :submitted]
                                          
                                          ] db  id)
                             )))
    :project/id id))







(pc/defresolver finance-color-resolver  [{:keys [db]} {:project/keys [id]}]  
  {::pc/input  #{:project/id}
   ::pc/output [:gov-review-week/finance-color :project/id]}
  (assoc (last (sort-by :gov-review-week/week
                        (map first (d/q '[:find (pull ?gw [:gov-review-week/finance-color :gov-review-week/week])
                                          :in $ ?id
                                          
                                          :where
                                          [?p :project/id ?id]
                                          [?gw :gov-review-week/project ?p]
                                          [?gw :gov-review-week/status :submitted]
                                          
                                          ] db  id)
                             )))
    :project/id id))



(pc/defmutation set-project-lead [{:keys [db connection]} {:keys [project-info/id lead-id]}]
  {::pc/sym`set-project-lead
   ::pc/params [:project-info/id :lead-id]
   ::pc/output [:entity]}
  (let [{pid :db/id :as entity} (d/entity db [:project-info/id (api/uuid id)])
        {lid :db/id} (d/entity db [:resource/id (api/uuid lead-id)])]

    (println "YOOOOOOO" lid "YOOO" pid)
    (if (not (nil? entity))
      @(d/transact (d/connect db-url) [{:db/id pid
                                :project-info/project-lead lid
                               }])
      @(d/transact connection [{:db/id "new"
                               :project-info/project-lead [:resource/id lead-id]
                               :project-info/id id}]))

    
    {:entity (nil? entity)}))



(pc/defresolver project-status-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [:project-info/status]}
  {:project-info/status
   (d/q '[:find ?status .;?rn ?re
          :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
          :where
          [?p :project-info/status ?status]
          [?p :project-info/id ?id]
          ]  db (api/uuid id))})

(pc/defmutation set-project-status [{:keys [db connection]} {:keys [project-info/id status]}]
  {::pc/sym`set-project-status
   ::pc/params [:project-info/id :status]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/status status
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/status status
                               :project-info/id (api/uuid id)}]))

    
    {:new-entity? (nil? entity)}))


(pc/defresolver project-phase-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [:project-info/phase]}
  {:project-info/phase
   (d/q '[:find ?phase .;?rn ?re
          :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
          :where
          [?p :project-info/phase ?phase]
          [?p :project-info/id ?id]
          ]  db (api/uuid id))})


(pc/defmutation set-project-phase [{:keys [db connection]} {:keys [project-info/id phase]}]
  {::pc/sym`set-project-phase
   ::pc/params [:project-info/id :phase]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/phase phase
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/phase phase
                               :project-info/id (api/uuid id)}]))

    
    {:entity (nil? entity)}))


(pc/defmutation submit-current-gov-review-week [{:keys [db connection]} {:keys [gov-review-week resource-id project/id]}]
  {::pc/sym `submit-current-gov-review-week
   ::pc/params [:gov-review-week :resource-id :project/id]
   ::pc/output [:gov-review-week/week
                :gov-review-week/status
                :gov-review-week/exec-summary-text
                :gov-review-week/exec-summary-color
                
                :gov-review-week/client-relationship-text
                :gov-review-week/client-relationship-color
                :gov-review-week/finance-text
                :gov-review-week/finance-color
                :gov-review-week/scope-schedule-text
                :gov-review-week/scope-schedule-color
                {:gov-review-week/submitted-by
                 [:resource/id
                  ]} 
                :gov-review-week/submitted-at]}

  (let [week (:gov-review-week/week gov-review-week)
        now (t/inst (t/now))

        eid (d/q '[:find ?e . 
                   :in $ ?id ?week
                   :where
                   [?e :gov-review-week/week ?week]
                   [?e :gov-review-week/project ?p]
                   [?p :project/id ?id]
                   ] db id week )

        pid (d/q '[:find ?e . 
                   :in $ ?id 
                   :where                            
                   [?e :project/id ?id]
                   ] db id )
        
        tx (cond-> gov-review-week
             true (assoc :db/id eid)
             resource-id  (assoc :gov-review-week/submitted-by [:resource/id resource-id])
             true (assoc :gov-review-week/submitted-at now :gov-review-week/status :submitted))]

    
    @(d/transact connection
                 [tx])
    
    #_(ffirst (d/q '[:find (pull ?e [:gov-review-week/week
                                   :gov-review-week/status
                                   :gov-review-week/exec-summary-text
                                   :gov-review-week/exec-summary-color
                                   
                                   :gov-review-week/client-relationship-text
                                   :gov-review-week/client-relationship-color
                                   :gov-review-week/finance-text
                                   :gov-review-week/finance-color
                                   :gov-review-week/scope-schedule-text
                                   :gov-review-week/scope-schedule-color
                                   {:gov-review-week/submitted-by
                                    [:resource/id]}
                                     :gov-review-week/submitted-at])  
                   :in $ ?id ?week
                   :where
                   [?e :gov-review-week/week ?week]
                   [?e :gov-review-week/project ?p]
                   [?p :project/id ?id]
                   ] db id week )

              )
    (assoc tx :gov-review-week/submitted-by {:resource/id resource-id})))








(pc/defmutation get-or-create-current-gov-review-week [{:keys [db connection]} {:keys [gov-review-week/week project/id]}]
  {::pc/sym `get-or-create-current-gov-review-week
   ::pc/params [:gov-review-week/week :project/id]
   ::pc/output [:gov-review-week/week
                :gov-review-week/status
                :gov-review-week/exec-summary-text
                :gov-review-week/exec-summary-color
                {:gov-review-week/project
                 [:project/id
                  :project/name
                  :project/start-date
                  :project/last-published-date
                  :project/modified-date
                  :project/finish-date]}
                :gov-review-week/client-relationship-text
                :gov-review-week/client-relationship-color
                :gov-review-week/finance-text
                :gov-review-week/finance-color
                :gov-review-week/scope-schedule-text
                :gov-review-week/scope-schedule-color
                {:gov-review-week/submitted-by
                 [:resource/id
                  :resource/name
                  :resource/email-address
                  :resource/active?
                  :resource/profile]} 
                :gov-review-week/submitted-at]}

  (let [entity (d/q '[:find ?e . 
                      :in $ ?id ?week
                      :where
                      [?e :gov-review-week/week ?week]
                      [?e :gov-review-week/project ?p]
                      [?p :project/id ?id]
                      ] db id week)]
    
    (when (nil? entity)
      (let [tx

            {:db/id "new"
             :gov-review-week/week week
             :gov-review-week/project [:project/id id]
             :gov-review-week/status  (if (t/< week (t/inst (t/now))) :overdue :open)

             :gov-review-week/exec-summary-text ""
             :gov-review-week/exec-summary-color :orange
             
             :gov-review-week/client-relationship-text ""
             :gov-review-week/client-relationship-color :orange
             
             :gov-review-week/finance-text ""
             :gov-review-week/finance-color :orange
             
             :gov-review-week/scope-schedule-text ""
             :gov-review-week/scope-schedule-color :orange
             
             }
            ]
        
        @(d/transact (d/connect "datomic:dev://localhost:4334/one2")
                     [tx])))
    
    (ffirst (d/q '[:find (pull ?e [:gov-review-week/week
                            :gov-review-week/status
                            :gov-review-week/exec-summary-text
                            :gov-review-week/exec-summary-color
                            {:gov-review-week/project
                             [:project/id
                              :project/name
                              :project/start-date
                              :project/last-published-date
                              :project/modified-date
                              :project/finish-date]}
                            :gov-review-week/client-relationship-text
                            :gov-review-week/client-relationship-color
                            :gov-review-week/finance-text
                            :gov-review-week/finance-color
                            :gov-review-week/scope-schedule-text
                            :gov-review-week/scope-schedule-color
                            {:gov-review-week/submitted-by
                             [:resource/id
                              :resource/name
                              :resource/email-address
                              :resource/active?
                              :resource/profile]} 
                            :gov-review-week/submitted-at] )  
                   :in $ ?id ?week
                   :where
                   [?e :gov-review-week/week ?week]
                   [?e :gov-review-week/project ?p]
                   [?p :project/id ?id]
                   ] (d/db (d/connect "datomic:dev://localhost:4334/one2")) id week ))
    ))


(pc/defmutation get-or-create-gov-review-week [{:keys [db connection]} {:keys [gov-review-week/week project/id]}]
  {::pc/sym `get-or-create-gov-review-week
   ::pc/params [:gov-review-week/week :project/id]
   ::pc/output [:gov-review-week/week
                            :gov-review-week/status
                            :gov-review-week/exec-summary-text
                            :gov-review-week/exec-summary-color
                            {:gov-review-week/project
                             [:project/id
                              :project/name
                              :project/start-date
                              :project/last-published-date
                              :project/modified-date
                              :project/finish-date]}
                            :gov-review-week/client-relationship-text
                            :gov-review-week/client-relationship-color
                            :gov-review-week/finance-text
                            :gov-review-week/finance-color
                            :gov-review-week/scope-schedule-text
                            :gov-review-week/scope-schedule-color
                            {:gov-review-week/submitted-by
                             [:resource/id
                              :resource/name
                              :resource/email-address
                              :resource/active?
                              :resource/profile]} 
                            :gov-review-week/submitted-at]}

  (let [entity (d/q '[:find ?e . 
                      :in $ ?id ?week
                      :where
                      [?e :gov-review-week/week ?week]
                      [?e :gov-review-week/project ?p]
                      [?p :project/id ?id]
                      ] (d/db (d/connect "datomic:dev://localhost:4334/one2")) id week)]
    (println "WICKED" week "ID" id)
    (if (nil? entity)
      (let [tx

            {:db/id "new"
             :gov-review-week/week week
             :gov-review-week/project [:project/id id]
             :gov-review-week/status  (if (t/< week (t/inst (t/now))) :overdue :open)

             :gov-review-week/exec-summary-text ""
             :gov-review-week/exec-summary-color :orange

             
             :gov-review-week/client-relationship-text ""
             :gov-review-week/client-relationship-color :orange
             
             :gov-review-week/finance-text ""
             :gov-review-week/finance-color :orange
             
             :gov-review-week/scope-schedule-text ""
             :gov-review-week/scope-schedule-color :orange
             
             }
            ]
        
        @(d/transact connection
                     [tx])
        
        tx)
      (ffirst (d/q '[:find (pull ?e [:gov-review-week/week
                                     :gov-review-week/status
                                     :gov-review-week/exec-summary-text
                                     :gov-review-week/exec-summary-color
                                     {:gov-review-week/project
                                      [:project/id
                                       :project/name
                                       :project/start-date
                                       :project/last-published-date
                                       :project/modified-date
                                       :project/finish-date]}
                                     :gov-review-week/client-relationship-text
                                     :gov-review-week/client-relationship-color
                                     :gov-review-week/finance-text
                                     :gov-review-week/finance-color
                                     :gov-review-week/scope-schedule-text
                                     :gov-review-week/scope-schedule-color
                                     {:gov-review-week/submitted-by
                                      [:resource/id
                                       :resource/name
                                       :resource/email-address
                                       :resource/active?
                                       :resource/profile]} 
                                     :gov-review-week/submitted-at])  
                     :in $ ?id ?week
                     :where
                     [?e :gov-review-week/week ?week]
                     [?e :gov-review-week/project ?p]
                     [?p :project/id ?id]
                     ] (d/db (d/connect "datomic:dev://localhost:4334/one2")) id week )))))




(pc/defmutation set-functional-lead [{:keys [db connection]} {:keys [project-info/id lead-id]}]
  {::pc/sym`set-functional-lead
   ::pc/params [:project-info/id :lead-id]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/functional-lead [:resource/id (api/uuid lead-id)]
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/functional-lead [:resource/id (api/uuid lead-id)]
                               :project-info/id (api/uuid id)}]))

    
    {:entity (nil? entity)}))


(pc/defmutation set-project-entity [{:keys [db connection]} {:keys [project-info/id entity]}]
  {::pc/sym`set-project-entity
   ::pc/params [:project-info/id :entity]
   ::pc/output [:entity]}
  (let [entity2 (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity2))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/entity entity
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/entity entity
                               :project-info/id (api/uuid id)}]))

    
    {:entity (nil? entity2)}))


(pc/defmutation set-project-fluxod-name [{:keys [db connection]} {:keys [project-info/id name]}]
  {::pc/sym`set-project-fluxod-name
   ::pc/params [:project-info/id :name]
   ::pc/output [:entity]}
  (let [entity2 (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity2))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/fluxod-name name
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/fluxod-name name
                               :project-info/id (api/uuid id)}]))

    
    {:entity (nil? entity2)}))


(pc/defresolver project-fluxod-name-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [:project-info/fluxod-name]}
  {:project-info/fluxod-name
   (d/q '[:find ?name .;?rn ?re
          :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
          :where
          [?p :project-info/fluxod-name ?name]
          [?p :project-info/id ?id]] db (api/uuid id))})



(pc/defresolver project-entity-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [:project-info/entity]}
  {:project-info/status
   (d/q '[:find ?entity .;?rn ?re
          :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
          :where
          [?p :project-info/entity ?entity]
          [?p :project-info/id ?id]
          ]  db (api/uuid id))})


(pc/defresolver project-lead-resolver [{:keys [connection db]} {:keys [project/id]}]
  {::pc/input  #{:project/id}
   ::pc/output [{:project-info/project-lead [:resource/id]} :project/id]
   }
  (let [r {:project-info/project-lead
           (ffirst (d/q '[:find (pull ?r [:resource/id])
                          
                          :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
                          :where
                          [?p :project-info/project-lead ?r]
                          [?r :resource/id ?rid]
                          
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
                          [?p :project-info/id ?id]
                          ]  db id))}
        ]
    
    (assoc r :project/id id)))


#_(pc/defresolver project-lead-resolver [{:keys [connection db]} input]
  {::pc/input  #{:project/id}
   ::pc/output [{:project-info/project-lead [:resource/id]}]
   ::pc/transform pc/transform-batch-resolver}
  
  (mapv (fn [{:keys [project/id]}]
          {:project-info/project-lead
           {:resource/id #uuid "1fc7ae1a-c8df-e911-b08c-00155de0701e" }} ) input))



(pc/defmutation set-technical-lead [{:keys [db connection]} {:keys [project-info/id lead-id]}]
  {::pc/sym`set-technical-lead
   ::pc/params [:project-info/id :lead-id]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-info/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-info/id (api/uuid id)]
                               :project-info/technical-lead [:resource/id (api/uuid lead-id)]
                               }])
      (d/transact connection [{:db/id "new"
                               :project-info/technical-lead [:resource/id (api/uuid lead-id)]
                               :project-info/id (api/uuid id)}]))
    
    {:entity (nil? entity)}))




(pc/defresolver functional-lead-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [{:project-info/functional-lead [:resource/id]}]}
  {:project-info/functional-lead
   {:resource/id
    (d/q '[:find ?rid .;?rn ?re
           :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
           :where
           [?p :project-info/functional-lead ?r]
           [?r :resource/id ?rid]
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
           [?p :project-info/id ?id]
           ]  db (api/uuid id))}})


(pc/defresolver technical-lead-resolver [{:keys [connection db]} {:keys [project-info/id]}]
  {::pc/input  #{:project-info/id}
   ::pc/output [{:project-info/technical-lead [:resource/id]}]}
  {:project-info/technical-lead
   {:resource/id
    (d/q '[:find ?rid .;?rn ?re
           :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
           :where
           [?p :project-info/technical-lead ?r]
           [?r :resource/id ?rid]
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
           [?p :project-info/id ?id]
           ]  db (api/uuid id))}})




(pc/defresolver made-up-resolver [env {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/made-up]}
  {:project-info/made-up 44})


(pc/defresolver made-up-resolver2 [{:keys [db]} {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/finish-date]}
  {:project-info/finish-date (d/q  '[:find  ?ps .
                                      
                                      :where
                                      [?p :project/id ?id ]
                                      [?p :project/finish-date ?ps]
                                      
                                      ] db id) })

(pc/defresolver start-date-resolver [{:keys [db]} {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/start-date]}

  {:project-info/start-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/start-date ?ps]
           
           ] db id)})

(pc/defresolver modified-date-resolver [{:keys [db]} {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/modified-date]}

  {:project-info/modified-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/modified-date ?ps]
           
           ] db id)})

(pc/defresolver last-published-date-resolver [{:keys [db]} {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/last-published-date]}

  {:project-info/last-published-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/last-published-date ?ps]
           
           ] db id)})










(pc/defresolver name-resolver [{:keys [db]} {:keys [project-info/id]}]
  {::pc/input #{:project-info/id}
   ::pc/output [:project-info/name]}

  
  {:project-info/name
   
   (d/q  '[:find  ?ps .
           :in $ ?id
           :where
           [?p :project/id ?id ]
           [?p :project/name ?ps]
           
           ] db (api/uuid id))})

(def alias-project-id (pc/alias-resolver2 :project/id :project-info/id))


(pc/defresolver project-resolver [env {:keys [project/id]}]
  {::pc/input #{:project/id}
   ::pc/output [:project/id :project/name  :project/modified-date :project/last-published-date 
                :project/created-date]}
  (d/q  '[:find ?pi ?pn  ?pm ?pl  ?pc ?pw
          :keys project/id project/name  project/modified-date project/last-published-date 
          project/created-date project/work
          :in $ ?pi
          :where
          
          [?p :project/id ?pi]
          [?p :project/name ?pn]
          
          [?p :project/modified-date ?pm]
          [?p :project/last-published-date ?pl]
          
          [?p :project/created-date ?pc]
          [?p :project/work ?pw]
          
          ] (d/db conn) id))



(pc/defresolver project-name-resolver [env {:keys [project/id]}]
  {::pc/input #{:project/id}
   ::pc/output [:project/name]}
  {:project/name
   (d/q  '[:find  ?pn .
           :in $ ?id
           :where
           
           [?p :project/id ?id]
           [?p :project/name ?pn]
           
           
           ] (d/db conn) id)})









(pc/defresolver all-projects-resolver [env _]
  {::pc/output [{:all-projects [:project/id :project/name :project/start-date :project/modified-date]}]}
  (let [id (get-in env [:ast :params :resource/id])]
    {:all-projects (d/q  '[:find ?pi
                           :keys project/id
                           :where
                           
                           [?p :project/id ?pi]
                           ] (d/db conn))}))







(pc/defresolver all-admin-projects [{:keys [connection db]} _]
  {::pc/output [{:all-admin-projects  [:project/id]}]}
  (let []
    {:all-admin-projects
     (mapv first (d/q  '[:find (pull ?p [:project/id]) 
                         
                         :where
                                        ;[?gw :gov-review-week/exec-summary-color ?c]
                                        ;[?gw :gov-review-week/project ?p]
                         [?p :project/id ?pi]
                         
                         ] (d/db (d/connect "datomic:dev://localhost:4334/one2"))))}))




(pc/defresolver projects-resolver [env _]
  {::pc/output [{:projects [:project/id :project/name]}]}
  (let [id (get-in env [:ast :params :resource/id])]
    {:projects (map (fn [row]
                      (zipmap [:project/name :project/id :project/modified-date :project/last-published-date] row))
                    (d/q  '[:find ?pn ?pi ?last-modified ?last-published
                            :in $ ?id
                            
                            :where
                            [?p :project/name ?pn]
                            [?p :project/id ?pi]
                            [?p :project/modified-date ?last-modified]
                            [?p :project/last-published-date ?last-published]
                            
                            [?p :project/assignments ?a]
                            [?a :assignment/resource ?r]
                            [?r :resource/id ?id]] (d/db conn) id))}))




;; TODO fix this
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

(pc/defresolver assignments-resolver [{:keys [connection db] :as env} _]
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
                                   ] (d/db (d/connect "datomic:dev://localhost:4334/one2")) resource-id project-id)))}))






#_(map (fn [row] (zipmap [:assignment/id :assignment/name :assignment/day :assignment/work] row))
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
                   ] (d/db (d/connect "datomic:dev://localhost:4334/one2"))
                     
                     )))

;; (d/q  '[:find ?pn ?pi
;;         :in $ ?id
        
;;         :where
;;         [?p :project/name ?pn]
;;         [?p :project/id ?pi]
;;         [?p :project/assignments ?a]
;;         [?a :assignment/resource ?r]
;;         [?r :resource/id ?id]] (d/db (d/connect "datomic:dev://localhost:4334/one2")) #uuid "65045544-f9d3-e911-b092-00155de43b0b")




;; delete projects
;; (d/transact
;;    (d/connect "datomic:dev://localhost:4334/one2")

;;    (mapv
;;     (fn [id]

;;       [:db/retractEntity id])

;;     (d/q '[:find [?e ...]

;;            :where
;;            [?e :project/name ?n]

;;            ] (d/db (d/connect "datomic:dev://localhost:4334/one2")))))

;; seed projects
;(d/transact (d/connect "datomic:dev://localhost:4334/one2") all-projects)


;; restart
;(user/restart)



                                        ;#uuid "65045544-f9d3-e911-b092-00155de43b0b"


            
            

(def resolvers  [alias-project-info-project-panel projects-resolver assignments-resolver assignment-resolver resource-resolver project-resolver
                 
                 all-projects-resolver made-up-resolver made-up-resolver2  #_alias-project-id start-date-resolver #_finish-date-resolver name-resolver modified-date-resolver last-published-date-resolver 
                 set-project-lead set-functional-lead functional-lead-resolver functional-lead-resolver set-functional-lead technical-lead-resolver set-technical-lead set-project-status project-status-resolver 
                 set-project-phase project-phase-resolver set-project-entity project-entity-resolver project-fluxod-name-resolver set-project-fluxod-name get-or-create-gov-review-week get-or-create-current-gov-review-week
                 submit-current-gov-review-week
                 finance-color-resolver
                 project-lead-resolver
                 client-relationship-color-resolver
                 overall-exec-summary-color-resolver
                 scope-schedule-color-resolver

                 project-name-resolver
                 
                 all-admin-projects
                 ])
