(ns app.model.database
  "This is a mock database implemented via Datascript, which runs completely in memory, has few deps, and requires
  less setup than Datomic itself.  Its API is very close to Datomics, and for a demo app makes it possible to have the
  *look* of a real back-end without having quite the amount of setup to understand for a beginner."
  (:require
   [datomic.api :as d]
   [datascript.core :as d2]
    [mount.core :refer [defstate]]))

;; In datascript just about the only thing that needs schema
;; is lookup refs and entity refs.  You can just wing it on
;; everything else.
(def schema {:account/id {:db/cardinality :db.cardinality/one
                          :db/unique      :db.unique/identity}})

(defn new-database [] (d2/create-conn schema))



(def db-url "datomic:dev://localhost:4334/one")

(d/create-database db-url)

(defstate conn :start (d/connect db-url))
