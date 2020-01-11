(ns app.model.resource
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   
   [datomic.api :as d]))




(pc/defresolver resource-resolver [env {:resource/keys [id]}]
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

                
                ] (d/db conn) id)))



(pc/defresolver all-resources-resolver [env _]
  {::pc/output [{:resource/all-resources [:resource/id]}]}
  {:resource/all-resources (flatten (seq (d/q  '[:find ?f
                                                 :keys resource/id
                                                 :where
                                                 [?e :resource/id ?f]
                                                 ] (d/db conn))))})

(def resolvers  [resource-resolver all-resources-resolver])
