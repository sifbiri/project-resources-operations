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

