(ns run
  (:require ;[com.wsscode.pathom.connect :as pc]
                                        ; [taoensso.timbre :as log]
   
   
   
   
   
   
                                        ;[datomic.client.api :as d]
   [datomic.api :as d]
   [app.model.api :as api]
   
   
   
                                        ;[clojure.data.generators :as gen]
   
   [clojure.pprint :as pp]
                                        ;[app.model.database :as db :refer [conn]]
   
   [clojure.core :refer :all]))





(println "delete projects...........")

(d/transact
 (d/connect "datomic:dev://localhost:4334/one2")

 (mapv
  (fn [id]

    [:db/retractEntity id])

  (d/q '[:find [?e ...]

         :where
         [?e :project/name ?n]

         ] (d/db (d/connect "datomic:dev://localhost:4334/one2")))))

(println "seed projects...............")
@(d/transact (d/connect "datomic:dev://localhost:4334/one2") (api/get-all-projects))

(println "Done....................")
(System/exit 0)
