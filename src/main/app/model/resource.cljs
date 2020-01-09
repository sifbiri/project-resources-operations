(ns app.model.resource
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            ;[app.ui.root :refer [ResourceCheckboxItem]]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]))




(defmutation create-resource-options [_]
  (action [{:keys [state]}]
          (let [resources (sort-by :resource/name (vals (get @state :resource/id)))
                resources-reduced (reduce (fn [r m] (when (:resource/name m)
                                                      (conj r m))) [] resources)
                r (into []
                        (map #(set/rename-keys % {:resource/id   :checkbox/value
                                                  ;:resource/email-address :checkbox/key
                                                  :resource/name :checkbox/label})
                             resources-reduced))]
            
            (swap! state assoc :resource/options r)
            

            

            (let [ResourceCheckboxItem (comp/registry-key->class :app.ui.root/ResourceCheckboxItem)
                  ResourcesCheckboxes (comp/registry-key->class :app.ui.root/ResourcesCheckboxes)]

              (swap! state merge/merge-component  ResourcesCheckboxes {:list/items r :list/all-checked? false})

              (doseq [v r]
                
                (swap! state merge/merge-component ResourceCheckboxItem (assoc v :checkbox/checked? false)) )))))
