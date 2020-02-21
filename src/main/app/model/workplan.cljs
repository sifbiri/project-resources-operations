(ns app.model.workplan
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [app.application :as a :refer [SPA]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [tick.alpha.api :as t]
            
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defn
  dates-from-to
  [workplan-start workplan-end]
  (loop [current (t/date-time (t/in workplan-start "GMT"))
         end (t/+ (t/date-time (t/in workplan-end "GMT"))
                  (t/new-period 1 :months))
         res []]
    (if (t/> current end)
      res
      (recur (t/+ current (t/new-period 1 :months))
             end
             (conj res current)))))

