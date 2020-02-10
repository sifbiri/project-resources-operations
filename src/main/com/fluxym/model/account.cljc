(ns com.fluxym.model.account
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]))

(defattr id :account/id :uuid
  {::attr/identity? true
   :com.fulcrologic.rad.database-adapters.datomic/schema :dev})

(defattr name2 :account/name2 :string
  {:com.fulcrologic.rad.database-adapters.datomic/schema :dev
   :com.fulcrologic.rad.database-adapters.datomic/entity-ids #{:account/id}
   ::attr/required? true})

(def attributes [id name2])
(def resolvers [])
