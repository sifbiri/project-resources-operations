(ns app.model.resource
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]))




(defmutation create-resource-options [_]
  (action [{:keys [state]}]
          (let [resources (sort-by :resource/name (vals (get @state :resource/id)))
                resources-reduced (reduce (fn [r m] (when (:resource/name m)
                                                    (conj r m))) [] resources)]
            (js/console.log "RESOURCES " resources-reduced)
            (swap! state assoc :resource/options (into []
                                                       (map #(set/rename-keys % {:resource/id   :value
                                                                                 :resource/email-address :key
                                                                                 :resource/name :text}))
                                                       resources-reduced)))))
