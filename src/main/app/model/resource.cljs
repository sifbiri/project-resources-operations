(ns app.model.resource
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            ;[app.ui.root :refer [ResourceCheckboxItem]]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            ;[app.ui.users :as users]
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
                             resources-reduced))


                r2 (into []
                         (map #(set/rename-keys % {:resource/id   :value
                                        ;:resource/email-address :checkbox/key
                                                :resource/name :text})
                           resources-reduced))]
            
            (swap! state assoc :resource/options r)
            (swap! state assoc :resource/options2 r2)
            

            

            (let [ResourceCheckboxItem (comp/registry-key->class :app.ui.root/ResourceCheckboxItem)
                  ResourcesCheckboxes (comp/registry-key->class :app.ui.root/ResourcesCheckboxes)]
              (js/console.log "PRINT" (vals (:resource/id @state)))
              (swap! state merge/merge-component  ResourcesCheckboxes
                     {:list/show-more? true :list/items (vals (:resource/id @state)) :list/all-checked? false}
                      )

              (doseq [v r]
                
                (swap! state merge/merge-component ResourceCheckboxItem (assoc v :ui/checked? false)
                       ;; TODO take this off ? 
                       :append [:component/id :checkboxes :list/items]

                       )

                )))))



(defmutation set-resource-profile [{:keys [id value]}]
  (action [{:keys [state]}]
          (let [tasks (vals (get @state :task/id))]
            (swap! state assoc-in [:resource/id id :resource/profile] value)))

  (remote [env]
          (let [Resource (comp/registry-key->class :app.ui.users/Resource)]
            true)))


(defmutation set-resource-active? [{:keys [id value]}]
  (action [{:keys [state]}]
          (let [tasks (vals (get @state :task/id))]
            (swap! state assoc-in [:resource/id id :resource/active?] value)
            (swap! state assoc-in [:checkbox/id id :resource/active?] value)))

  (remote [env]
          true))
