(ns app.model.work-line
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]))


(defmutation create-project-options [_]
  (action [{:keys [state]}]
          (let [projects (get @state :project/all-projects)]
            (js/console.log "projects..." projects)

            (swap! state assoc :project/project-options (into []
                                                                (map #(set/rename-keys % {:project/id   :value
                                                                                          :project/name :text}))
                                                                projects)))))

(defn work-line-valid?
  "A user-written item validator (by field)"
  [{:work-line/keys [hours project] :as workline} field] true
  (try
    (case field
      :work-line/hours  (> hours 0)
 ;     :item/price (math/> price 0);
  ;    :item/in-stock (math/>= in-stock 0);
      true)
    (catch :default _
     true)
    ))

(def work-line-validator (fs/make-validator work-line-valid?))

(defmutation save-work-line
  "Unchecked mutation. Sends the given diff to the server without checking validity. See try-save-item."
  [{:item/keys [id]
    :keys      [diff]
    :as        params}]
  (action [{:keys [app state]}]
          (swap! state assoc-in [:work-line/id id :ui/saving?] true))
  (remote [env] true)
  (ok-action [{:keys [state]}]
             (swap! state (fn [s]
                            (-> s
                                (update-in [:work-line/id id] assoc :ui/new? false :ui/saving? false)
                                (fs/entity->pristine* [:work-line/id id])))))
  (error-action [{:keys [state]}]
                (js/alert "Failed to save item")
                (swap! state (fn [s]
                               (-> s
                                   (update-in [:work-line/id id] assoc :ui/saving? false))))))



(defmutation remove-work-line [{:work-line/keys [id]}]
  
  (action [{:keys [state]}]
          (js/console.log "remove work line")
          (swap! state (fn [s]
                         (-> s
                             
                             (merge/remove-ident* [:work-line/id id] [:component/id :work-day :work-day/all-work-lines])
                             (update :work-line/id dissoc id))))))

(defmutation reset-form [{:work-line/keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (fs/pristine->entity* [:work-line/id id])
                             #_(update :work-line/id dissoc id))))))

(defmutation try-save-work-line [{:work-line/keys [id]
                                  :keys      [diff]
                                  :as        params}]
  (action [{:keys [app state]}]

          (let [state-map       @state
                ident           [:work-line/id id]
                completed-state (fs/mark-complete* state-map ident)
                work-line            (get-in completed-state ident)
                WorkLine (comp/registry-key->class :app.ui.root/WorkLine)
                item-props      (fdn/db->tree (comp/get-query WorkLine) work-line completed-state)
                valid?          (= :valid  (work-line-validator item-props))]


            (if true
              (comp/transact! app [(save-work-line  params)])
              (reset! state completed-state)))))

(defmutation remove-item [{:work-line/keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (merge/remove-ident* [:work-line/id id] [:component/id :work-day :work-day/all-work-lines ])
                             (update :work-line/id dissoc id))))))


(comment

 #_ (swap! state (fn [s]
                         (-> s
                             (fs/pristine->entity* [:work-line/id id])
                             #_(update :work-line/id dissoc id))))


 (as-> {} s
   (merge/merge-component! s WorkDay)))


