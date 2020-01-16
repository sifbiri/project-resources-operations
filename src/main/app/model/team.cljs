(ns app.model.team
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]))


(defmutation add-team-member [{:keys [team-id team-member-id]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (update-in [:team/id team-id :team/resources] conj [:resource/id team-member-id] ))))
          )
  (remote [env] true))


(defmutation delete-team-member [{:keys [team-id team-member-id]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (merge/remove-ident* [:resource/id team-member-id] [:team/id team-id :team/resources])))))
  (remote [env] true))


(defmutation set-team-name [{:keys [team-id name]}]
  (action [{:keys [state]}]
          (let [TeamCheckbox (comp/registry-key->class :app.ui.root/TeamCheckbox)]
            (js/console.log "LOG" team-id)
           (swap! state (fn [state]
                          (-> state
                              (assoc-in [:team/id team-id :team/name] name)
                              )))
           ;; (swap! state  merge/merge-component TeamCheckbox
           ;;        {:db/id team-id :team/name name }
                  
           ;;        )
           ))
  (remote [env] true))



(defmutation merge-team-checkboxes [_]
  (action [{:keys [state app]}]
          (js/console.log "MERGE" (vals (get @state :team/id)))
          (let [teams (vals (get @state :team/id))
                TeamCheckbox (comp/registry-key->class :app.ui.root/TeamCheckbox)]
            (doseq [team teams]
              (do
                (swap! state merge/merge-component TeamCheckbox  (select-keys (assoc team :ui/checked? false) [:db/id :ui/checked? :team/name]
                                                                              ) :target [:component/id :workplan :workplan/team-checkboxes])) ))))

(defmutation delete-team [{:keys [team-id]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (merge/remove-ident* [:team/id team-id] [:component/id :admin-teams :teams/teams])
                             (merge/remove-ident* [:team-checkbox/id team-id] [:component/id :workplan :workplan/team-checkboxes])
                             (update :team/id dissoc team-id)
                             ))))
  (remote [env] true))




(defmutation set-team-type [{:keys [team-id type]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:team/id team-id :team/type] type)))))
  (remote [env] true))


(defmutation set-team-lead [{:keys [team-id lead-id]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:team/id team-id :team/lead] [:resource/id lead-id])))))
  (remote [env] true))





