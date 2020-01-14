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
          
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:team/id team-id :team/name] name)))))
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





