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

