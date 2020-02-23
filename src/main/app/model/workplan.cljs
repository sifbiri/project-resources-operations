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
            [clojure.string :as str]
            
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defn same-month? [date1 date2]
  (= (-> date1 t/date-time t/month ) (-> date2 t/date-time t/month)))



(defn
  week-number
  [inst]
  (let [[year month day] 
        (mapv
         js/parseInt
         (str/split (t/date inst) #"-")
         )]
    (goog.date.getWeekNumber year (- month 1) day)
    )
  )
(defn same-week? [date1 date2]
  (= (week-number date1) (week-number date2)))

(defn
  dates-from-to 
  [workplan-start workplan-end {:keys [dates] :or {dates :months }}]
  (if (= dates :weeks)
    (loop [current (t/date-time (t/in workplan-start "GMT"))
           end (t/date-time (t/in workplan-end "GMT"))
           res []]
      (if (same-week? current end)
        (conj res end)
        (recur (t/+ current (t/new-period 1 :weeks))
               end
               (conj res current))))
    
    (loop [current (t/date-time (t/in workplan-start "GMT"))
           end (t/date-time (t/in workplan-end "GMT"))
           res []]
      (if (same-month? current end)
        (conj res end)
        (recur (t/+ current (t/new-period 1 :months))
               end
               (conj res current))))))


(defn
  weeks-from-to
  [from to])


(defn
  pad-month-cells [start end]
  (loop [start (t/date start)
         end (t/date end)
         count 0]
    (if (same-month? start end)
      (+ count 1)
      (recur (t/+ start (t/new-period 1 :months))
             end
             (+ 1 count)))))

(defmutation set-workplan-count  [{:keys [count]}]
  (action [{:keys [state]}]
          (ns/swap!->
           state
           (assoc :ui/workplan-count count)))
  (remote [env] false))

(defmutation set-workplan-count-init [{:keys [ident]}]
  (action [{:keys [state ref]}]
          
          (let [min-date (ns/get-in-graph @state (conj ident :workplan/min-date))
                max-date (ns/get-in-graph @state (conj ident :workplan/max-date))
                count (count (dates-from-to min-date max-date {:dates :weeks}))]
            (ns/swap!->
             state
             (assoc :ui/workplan-count count)))))



