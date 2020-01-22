(ns app.model.project
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   [app.model.api :as api]
   
   [datomic.api :as d]))

(def db-url "datomic:dev://localhost:4334/one2")


(def conn2 (d/connect db-url))

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



(pc/defmutation set-project-lead [{:keys [db connection]} {:keys [project-panel/id lead-id]}]
  {::pc/sym`set-project-lead
   ::pc/params [:project-panel/id :lead-id]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-panel/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-panel/id (api/uuid id)]
                               :project-panel/project-lead [:resource/id (api/uuid lead-id)]
                               }])
      (d/transact connection [{:db/id "new"
                               :project-panel/project-lead [:resource/id (api/uuid lead-id)]
                               :project-panel/id (api/uuid id)}]))

    
    {:entity (nil? entity)}))


(pc/defmutation set-functional-lead [{:keys [db connection]} {:keys [project-panel/id lead-id]}]
  {::pc/sym`set-functional-lead
   ::pc/params [:project-panel/id :lead-id]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-panel/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-panel/id (api/uuid id)]
                               :project-panel/functional-lead [:resource/id (api/uuid lead-id)]
                               }])
      (d/transact connection [{:db/id "new"
                               :project-panel/functional-lead [:resource/id (api/uuid lead-id)]
                               :project-panel/id (api/uuid id)}]))

    
    {:entity (nil? entity)}))



(pc/defresolver project-lead-resolver [{:keys [connection db]} {:keys [project-panel/id]}]
  {::pc/input  #{:project-panel/id}
   ::pc/output [{:project-panel/project-lead [:resource/id]}]}
  {:project-panel/project-lead
   {:resource/id
    (d/q '[:find ?rid .;?rn ?re
           :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
           :where
           [?p :project-panel/project-lead ?r]
           [?r :resource/id ?rid]
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
           [?p :project-panel/id ?id]
           ]  db (api/uuid id))}})



(pc/defmutation set-technical-lead [{:keys [db connection]} {:keys [project-panel/id lead-id]}]
  {::pc/sym`set-technical-lead
   ::pc/params [:project-panel/id :lead-id]
   ::pc/output [:entity]}
  (let [entity (d/entity db [:project-panel/id (api/uuid id)]) ]

    
    (if (not (nil? entity))
      (d/transact connection [{:db/id [:project-panel/id (api/uuid id)]
                               :project-panel/technical-lead [:resource/id (api/uuid lead-id)]
                               }])
      (d/transact connection [{:db/id "new"
                               :project-panel/technical-lead [:resource/id (api/uuid lead-id)]
                               :project-panel/id (api/uuid id)}]))
    
    {:entity (nil? entity)}))




(pc/defresolver functional-lead-resolver [{:keys [connection db]} {:keys [project-panel/id]}]
  {::pc/input  #{:project-panel/id}
   ::pc/output [{:project-panel/functional-lead [:resource/id]}]}
  {:project-panel/functional-lead
   {:resource/id
    (d/q '[:find ?rid .;?rn ?re
           :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
           :where
           [?p :project-panel/functional-lead ?r]
           [?r :resource/id ?rid]
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
           [?p :project-panel/id ?id]
           ]  db (api/uuid id))}})


(pc/defresolver technical-lead-resolver [{:keys [connection db]} {:keys [project-panel/id]}]
  {::pc/input  #{:project-panel/id}
   ::pc/output [{:project-panel/technical-lead [:resource/id]}]}
  {:project-panel/technical-lead
   {:resource/id
    (d/q '[:find ?rid .;?rn ?re
           :in $ ?id
                                        ;:keys resource/id resource/name resource/email-address
           :where
           [?p :project-panel/technical-lead ?r]
           [?r :resource/id ?rid]
                                        ;[?r :resource/name ?rn]
                                        ;[?r :resource/email-address ?re]
           [?p :project-panel/id ?id]
           ]  db (api/uuid id))}})




(pc/defresolver made-up-resolver [env {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/made-up]}
  {:project-panel/made-up 44})


(pc/defresolver made-up-resolver2 [{:keys [db]} {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/finish-date]}
  {:project-panel/finish-date (d/q  '[:find  ?ps .
                                      
                                      :where
                                      [?p :project/id ?id ]
                                      [?p :project/finish-date ?ps]
                                      
                                      ] db id) })

(pc/defresolver start-date-resolver [{:keys [db]} {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/start-date]}

  {:project-panel/start-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/start-date ?ps]
           
           ] db id)})

(pc/defresolver modified-date-resolver [{:keys [db]} {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/modified-date]}

  {:project-panel/modified-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/modified-date ?ps]
           
           ] db id)})

(pc/defresolver last-published-date-resolver [{:keys [db]} {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/last-published-date]}

  {:project-panel/last-published-date
   (d/q  '[:find  ?ps .
           
           :where
           [?p :project/id ?id ]
           [?p :project/last-published-date ?ps]
           
           ] db id)})










(pc/defresolver name-resolver [{:keys [db]} {:keys [project-panel/id]}]
  {::pc/input #{:project-panel/id}
   ::pc/output [:project-panel/name]}

  
  {:project-panel/name
   
   (d/q  '[:find  ?ps .
           :in $ ?id
           :where
           [?p :project/id ?id ]
           [?p :project/name ?ps]
           
           ] db (api/uuid id))})

(def alias-project-id (pc/alias-resolver2 :project/id :project-panel/id))


(pc/defresolver project-resolver [env {:keys [project/id]}]
  {::pc/input #{:project/id}
   ::pc/output [:project/id :project/name   :project/modified-date :project/last-published-date 
                :project/created-date :project/work]}
  (d/q  '[:find ?pi ?pn  ?pm ?pl  ?pc ?pw
          :keys project/id project/name  project/modified-date project/last-published-date 
          project/created-date project/work
          :where
          
          [?p :project/id ?pi]
          [?p :project/name ?pn]
          
          [?p :project/modified-date ?pm]
          [?p :project/last-published-date ?pl]
          
          [?p :project/created-date ?pc]
          [?p :project/work ?pw]
          
          ] (d/db conn)))




(pc/defresolver all-projects-resolver [env _]
  {::pc/output [{:all-projects [:project/id :project/name :project/start-date :project/modified-date]}]}
  (let [id (get-in env [:ast :params :resource/id])]
    {:all-projects (d/q  '[:find ?pi
                           :keys project/id
                           :where
                           
                           [?p :project/id ?pi]
                           ] (d/db conn))}))




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


            
            

(def resolvers  [projects-resolver assignments-resolver assignment-resolver resource-resolver project-resolver
                 all-projects-resolver made-up-resolver made-up-resolver2  alias-project-id start-date-resolver #_finish-date-resolver name-resolver modified-date-resolver last-published-date-resolver project-lead-resolver
                 set-project-lead set-functional-lead functional-lead-resolver functional-lead-resolver set-functional-lead technical-lead-resolver set-technical-lead])
