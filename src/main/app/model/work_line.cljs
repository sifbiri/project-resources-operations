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
          (let [projects (vals (get @state :project/id))]
             (js/console.log "projects..." projects)
;            (println "projects .... " projects)
            (swap! state assoc :project/options (into []
                                                      (map #(set/rename-keys % {:project/id   :value
                                                                                :project/name :text}))
                                                      projects)))))

(defn work-line-valid?
  "A user-written item validator (by field)"
  [{:work-line/keys [hours project] :as workline} field] true
  true)

(def work-line-validator (fs/make-validator work-line-valid?))

(defmutation add-form-config [{:keys [person-id]}]
  (action [{:keys [state]}]
          (let [WorkLine (comp/registry-key->class :app.ui.root/WorkLine) ]
            (swap! state (fn [s]
                          (-> s
                              (fs/add-form-config* WorkLine [:person/id person-id]) ; this will affect the joined phone as well
                              )))))) 

(defmutation save-work-line
  "Unchecked mutation. Sends the given diff to the server without checking validity. See try-save-item."
  [{:work-line/keys [id]
    :keys      [diff]
    :as        params}]
  (action [{:keys [app state]}]

          (swap! state assoc-in [:work-line/id id :ui/saving?] true)
          (swap! state assoc-in [:work-line/id id :ui/saved?] false)
                                        ;(swap! state fs/entity->pristine* [:work-line/id id])
          )
  
  (error-action [{:keys [state]}]
                (js/alert "Failed to save item")
                (swap! state (fn [s]
                               (-> s
                                   (update-in [:work-line/id id] assoc :ui/saving? false)))))
  
  (remote [env] true)

  (ok-action [{:keys [app state]}]
             (println "OK_ACTION" id)
             #_(comp/transact! app [(fs/reset-form! {:form-ident [:work-line/id id]})])
             (swap! state (fn [s]
                            (-> s
                                (fs/entity->pristine* [:work-line/id id])
                                (update-in [:work-line/id id] assoc :ui/new? false :ui/saving? false :ui/saved? true)
                                                             
                                )))
             ))



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
                work-line       (get-in completed-state ident)
                WorkLine        (comp/registry-key->class :app.ui.root/WorkLine)
                item-props      (fdn/db->tree (comp/get-query WorkLine) work-line completed-state)
                valid?          (= :valid  (work-line-validator item-props))]

            ;(js/console.log "valid?..." completed-state)
            ;(comp/transact! app [(fs/mark-complete! {:entity-ident [:work-line/id id]})])
            (js/console.log "valid?" valid?)
            (if valid?
              (comp/transact! app [(save-work-line  params)])
              (reset! state completed-state)))))

(defmutation remove-item [{:work-line/keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (merge/remove-ident* [:work-line/id id] [:component/id :work-day :work-day/all-work-lines ])
                             (update :work-line/id dissoc id))))))
