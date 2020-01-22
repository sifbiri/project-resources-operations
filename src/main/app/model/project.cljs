(ns app.model.project
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]))




(defmutation populate-projects [_]
  (action [{:keys [state]}]
          (let [projects (get @state :projects)]
            (println "HI THERE"  projects)
            
            (swap! state assoc-in [:component/id :resources :resources/projects] projects)
            (swap! state assoc  :projects []))))

(defmutation set-project-lead [{:keys [:project-panel/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/project-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))



(defmutation set-project-status [{:keys [project-panel/id status]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/status] status)))))
  (remote [env] true))

(defmutation set-project-entity [{:keys [project-panel/id entity]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/entity] entity)))))
  (remote [env] true))



(defmutation set-project-fluxod-name [{:keys [project-panel/id name]}]
  (action [{:keys [state]}]
          (let [TeamCheckbox (comp/registry-key->class :app.ui.root/TeamCheckbox)]
            
            (swap! state (fn [state]
                           (-> state
                               (assoc-in [:project-panel/id id :project-panel/fluxod-name] name)
                               )))
            ;; (swap! state  merge/merge-component TeamCheckbox
            ;;        {:db/id team-id :team/name name }
            
            ;;        )
            ))
  (remote [env] true))





(defmutation set-project-phase [{:keys [project-panel/id phase]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/phase] phase)))))
  (remote [env] true))


(defmutation set-functional-lead [{:keys [:project-panel/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/functional-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))

(defmutation set-technical-lead [{:keys [:project-panel/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-panel/id id :project-panel/technical-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))

