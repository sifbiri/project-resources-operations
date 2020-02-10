(ns com.fluxym.model.item
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]))

(defattr id :item/id :uuid
  {::attr/identity?                                      true
   :com.fulcrologic.rad.database-adapters.datomic/schema :dev})

(defattr item-name :item/name :string
  {:com.fulcrologic.rad.database-adapters.datomic/entity-ids #{:item/id}
   :com.fulcrologic.rad.database-adapters.datomic/schema     :dev})

(defattr all-items :item/all-items :ref
  {::attr/target    :item/id
   ::pc/output      [{:item/all-items [:item/id :item/name :item/price] }]
   ::pc/resolve     (fn [{:keys [query-params] :as env} _]
                      #?(:clj
                         {:item/all-items [{:item/name "1" :item/id 1 :item/price 11.03} {:item/name "2" :item/id 2 :item/price 22.2} {:item/name "3" :item/id 3 :item/price 9.5}]}))})

(def attributes [id item-name all-items])
