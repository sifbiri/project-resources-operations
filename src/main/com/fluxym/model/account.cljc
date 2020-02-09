(ns com.fluxym.model.account
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]))

(defattr id :account/id :uuid
  {::attr/identity? true})

(defattr account-name :account/name :string
  {::attr/required? true})

(def attributes [id name])
(def resolvers [])
