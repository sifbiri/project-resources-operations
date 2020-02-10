(ns com.fluxym.model.line-item
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]))

(defattr id :line-item/id :uuid
  {::attr/identity?                                      true
   :com.fulcrologic.rad.database-adapters.datomic/schema :env})

(defattr item :line-item/item :ref
  {::attr/target                                             :item/id
   ::attr/cardinality                                        :one
   :com.fulcrologic.rad.database-adapters.datomic/entity-ids #{:line-item/id}
   :com.fulcrologic.rad.database-adapters.datomic/schema     :env})

(defattr quantity :line-item/quantity :int
  {:com.fulcrologic.rad.database-adapters.datomic/entity-ids #{:line-item/id}
   :com.fulcrologic.rad.database-adapters.datomic/schema     :env})

(def attributes [id item quantity])
