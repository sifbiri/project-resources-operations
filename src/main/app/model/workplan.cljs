(ns app.model.workplan
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
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
  (and (= (-> date1 t/date-time t/month ) (-> date2 t/date-time t/month))
       (= (-> date1 t/date-time t/year ) (-> date2 t/date-time t/year ))))



(defn
  week-number
  [inst]
  (let [[year month day] 
        (mapv
         js/parseInt
         (str/split (t/date inst) #"-")
         )]
    (goog.date.getWeekNumber year (- month 1) day)
    ))

(defn same-week? [date1 date2]
  (and (= (week-number date1)
          (week-number date2))
       (= (t/year date1)
          (t/year date2))))

(defn timesheet-+
  ([] 0)
  ([t1] (+ (or (:timesheet/work-fluxod t1) 0)
           (or (:timesheet/work-ms t1) 0)))
  ([t1 t2]
     (+ (or (:timesheet/work-fluxod t1) 0)
        (or (:timesheet/work-fluxod t2) 0)
        (or (:timesheet/work-ms t1) 0)
        (or (:timesheet/work-ms t2) 0)))

  ([t1 t2 & more]
     (reduce (fn [acc t]
               (+ acc (timesheet-+ acc t) )) (timesheet-+ t1 t2) more)))



(defn my [x ]
  (+ 1 x))
(defn
  dates-from-to 
  [workplan-start workplan-end {:keys [dates] :or {dates :months }}]
  (if (= dates :weeks)
    (do (js/console.log "WE GOT START" workplan-start)
        (js/console.log "WE GOT END" workplan-end)

        (if
            (t/< (t/date-time workplan-start) (t/date-time workplan-end))
          
            (mapv second (t/divide (t/new-interval  (t/inst workplan-start)
                                                    (t/inst workplan-end))
                                 (t/new-duration 7 :days)))
          [])


        
     #_(conj (vec
            (take-while (fn [x]
                          (and
                           (not (same-week? (t/date-time x) (t/date-time workplan-end)))
                           (t/<= x (t/date-time workplan-end))))
                            (iterate #(t/+ (t/date-time %)
                                           (t/new-period 1 :weeks))
                                 (t/date-time workplan-start))))
           (t/date-time workplan-end))


     )

    #_(if
        (t/< (t/date-time workplan-start) (t/date-time workplan-end))
      
      (mapv second (t/divide (t/new-interval  (t/inst workplan-start) (t/inst workplan-end))
                             (t/new-duration 30 :days)))
      [])

    (conj (vec (take-while (fn [x]
                         (and
                          (not (same-month? (t/date-time x) (t/date-time workplan-end)))
                          (t/<= x (t/date-time workplan-end))))
                           (iterate #(t/+ (t/date-time %)
                                          (t/new-period 1 :months))
                                    (t/date-time workplan-start))))
          (t/date-time workplan-end))
    
    #_(loop [current (t/date-time (t/in workplan-start "GMT"))
           end (t/date-time (t/in workplan-end "GMT"))
           res []]
      (if (same-week? current end)
        (conj res end)
        (recur (t/+ current (t/new-period 1 :weeks))
               end
               (conj res current))))
    
    #_(loop [current (t/date-time (t/in workplan-start "GMT"))
           end (t/date-time (t/in workplan-end "GMT"))
           res []]
      (if (same-month? current end)
        (conj res end)
        (recur (t/+ current (t/new-period 1 :months))
               end
               (conj res current)))))

  )

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
                by-week? (ns/get-in-graph @state (conj ident :ui/by-week?))
                #_#_count (count (dates-from-to min-date max-date {:dates (if by-week? :weeks :months)}))]
            (ns/swap!->
             state
             #_(assoc :ui/workplan-count count)
             (assoc :ui/workplan-by-week? by-week?))
            (dr/target-ready! SPA ident ))))



(defn month-of-year
  [date]
	(str (t/month (t/date date)) " " (t/year (t/date date))))
