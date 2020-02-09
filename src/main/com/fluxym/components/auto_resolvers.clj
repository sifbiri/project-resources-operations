(ns com.fluxym.components.auto-resolvers
  (:require
   [com.fluxym.model :refer [all-attributes]]
   [mount.core :refer [defstate]]
   [com.fulcrologic.rad.resolvers :as res]
   [com.fulcrologic.rad.database-adapters.datomic :as datomic]
   [taoensso.timbre :as log]))

(defstate automatic-resolvers
  :start
  (vec
   (concat
    (res/generate-resolvers all-attributes)
   (datomic/generate-resolvers all-attributes :production))))
