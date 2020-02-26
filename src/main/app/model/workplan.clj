(ns app.model.workplan
  (:require
   [com.wsscode.pathom.connect :as pc]
   [clj-fuzzy.metrics :as fm]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [app.model.database :refer [conn]]
   [clojure.core.async :refer [go]]
   [clojure.set :as set]
   [app.model.api :as api]
   [dk.ative.docjure.spreadsheet :as s]
   [tick.alpha.api :as t]
   [clojure.string :as str]
   [clj-time.core :as tm]
   [datomic.api :as d]
   [app.model.database :refer [db-url]]
   [taoensso.timbre :as log])
  (:import
   (java.util Calendar)))








(defn week-number
 [inst]
 (apply str (take 2 (drop 5 (str (t/date inst))))))

(defn week-of-year
  [date]
	(let [cal (Calendar/getInstance)]
		(.setTime cal date)
		(.get cal Calendar/WEEK_OF_YEAR)))


(defn same-cell? [fluxod-ts ms-ts & {:keys [by-week?] :or {by-week? false}}]
  (let [fluxod-last (-> fluxod-ts :timesheet/end-fluxod)
        ms-first (-> ms-ts :timesheet/start-ms )]
    (when (and fluxod-last ms-first)
      (if by-week?
        (= (week-of-year fluxod-last)
           (week-of-year ms-first))
        (= (-> fluxod-last t/date t/month ) (-> ms-first t/date t/month))))))

(defn merge-timesheets
  [fluxod-ts ms-ts]
  (vec (concat (butlast fluxod-ts) [(merge (last fluxod-ts) (first ms-ts) )] (rest ms-ts))))

(pc/defresolver min-max-date [{:keys [connection db]} {:keys [workplan/id]}]
  {::pc/input #{:workplan/id}
   ::pc/output [:workplan/max-date :workplan/min-date]}
  {:workplan/max-date (last (sort (d/q '[:find [?date ...]
                                         :in $ ?id
                                         :where
                                         [?p :project/id ?id]
                                         [?p :project/assignments ?a]
                                         [?a :assignment/by-day ?date]]
                                       db
                                       id
                                       )))
   :workplan/min-date (first (sort (d/q '[:find  [?date ...]
                                          :in $  ?pid 
                                          :where
                                          [?fluxod :fluxod-ts/date ?date]
                                          
                                          [?fluxod :fluxod-ts/client ?client]
                                          [?fluxod :fluxod-ts/po ?fluxod-po]
                                          
                                          [?pinfo :project-info/fluxod-client-name ?client]
                                          [?pinfo :project-info/fluxod-project-names ?fluxod-po]
                                          [?pinfo :project-info/id ?pid]
                                          
                                          [?e :project/id ?pid]
                                          [?e :project/name ?name]
                                          ](d/db (d/connect db-url))
                                           id
                                           
                                           )))
   })



(def group-by-month 
  #(str (t/month (t/date (:date %)))))

(def group-by-week
  #(str (week-of-year (:date %)) " " (t/year (t/date (:date %)))))


(defn group-fluxod-timesheets
  [fluxod-timesheets & {:keys [by-week?] :or {by-week? false}}]
  (sort-by
   :timesheet/start-fluxod
   (reduce-kv (fn [acc month timesheets]
                (conj acc (reduce (fn [{:keys [timesheet/work-fluxod] :as m}
                                       timesheet]
                                    (assoc
                                        m
                                      :timesheet/work-fluxod
                                      (+ work-fluxod (:timesheet/work-fluxod timesheet)))
                                    ) {:timesheet/work-fluxod 0
                                       :timesheet/start-fluxod
                                       (-> timesheets first :date)
                                       :timesheet/end-fluxod
                                       (-> timesheets last :date)
                                       } timesheets )))

              [] (group-by (if by-week? group-by-week group-by-month ) fluxod-timesheets))))


(defn group-ms-timesheets
  [ms-timesheets & {:keys [by-week?] :or {by-week? false}}]
  (sort-by
   :timesheet/start-ms
   (reduce-kv (fn [acc month timesheets]
               (conj acc (reduce (fn [{:keys [timesheet/work-ms] :as m}
                                      timesheet]
                                   (assoc
                                       m
                                     :timesheet/work-ms ;; convert from hours to days  
                                     (+ work-ms (/ (:timesheet/work-ms timesheet) 8.0))))
                                 {:timesheet/work-ms 0
                                  :timesheet/start-ms
                                  (-> timesheets first :date)
                                  :timesheet/end-ms
                                  (-> timesheets last :date)
                                  } timesheets )))

              [] (group-by (if by-week? group-by-week group-by-month) ms-timesheets))))



(pc/defresolver resource-ts [{:keys [connection db] :as env} {:keys [resource-ts/id workplan/max-date workplan/min-date] workplan :workplan/id}]
  {::pc/input #{:resource-ts/id :workplan/id :workplan/max-date :workplan/min-date}
   ::pc/output [:resource-ts/id  :resource-ts/name :resource-ts/start-date
                :workplan/min-date
                :workplan/max-date
                :resource-ts/end-date
                {:resource-ts/timesheets [:timesheet/start-fluxod
                                          :timesheet/end-fluxod
                                          :timesheet/start-ms
                                          :timesheet/end-ms
                                          :timesheet/work-fluxod
                                          :timesheet/work-ms]}]}

  (do (println (-> env :query-params :by))
      (let [by-week? (= (-> env :query-params :by) :week)
          fluxod-timesheets (sort-by
                             :date
                             (d/q '[:find ?work-fluxod ?date ?fluxod-po
                                    :keys timesheet/work-fluxod date fluxod-po
                                    :in $ ?rid ?pid ?min-date ?max-date
                                    :where
                                    [?r :resource/id ?rid]
                                    
                                    [?act :fluxod-ts/activity-type ?t]
                                    [(= :mission ?t)]
                                    [?fluxod :fluxod-ts/resource-name ?fluxod-name]
                                    [?fluxod :fluxod-ts/days ?work-fluxod]
                                    [?fluxod :fluxod-ts/date ?date]
                                    
                                    [?fluxod :fluxod-ts/client ?client]
                                    [?fluxod :fluxod-ts/po ?fluxod-po]
                                    
                                    [?pinfo :project-info/fluxod-client-name ?client]
                                    [?pinfo :project-info/fluxod-project-names ?fluxod-po]
                                    [?pinfo :project-info/id ?pid]
                                    
                                    [?e :project/id ?pid]
                                    [?e :project/name ?name]
                                        ;[(tick.alpha.api/> ?date #inst "2019-11-20T00:00:00.000-00:00")]
                                    [(tick.alpha.api/>= ?date ?min-date)]
                                    [(tick.alpha.api/<= ?date ?max-date)]
                                    
                                    [?r :resource/fluxod-name ?fluxod-name]
                                    ] db
                                      (log/spy :info id)
                                      (log/spy :info workplan)
                                      (log/spy :info min-date)
                                      (log/spy :info max-date)))
            fluxod-last-date (log/spy :info (-> fluxod-timesheets last :date))

          ms-timesheets
          (sort-by
           :date
           (d/q '[:find ?work ?date
                  :keys timesheet/work-ms date 
                  :in $ ?rid ?pid ?fluxod-last-date ?max-date
                  :where
                  [?r :resource/id ?rid]
                  ;[?r :resource/name ?rn]
                  
                  
                  ;[?pinfo :project-info/fluxod-client-name ?client]
                  ;[?pinfo :project-info/fluxod-project-names ?fluxod-po]
                  ;[?pinfo :project-info/id ?pid]
                  
                  [?e :project/id ?pid]
                  [?e :project/assignments ?a]
                  
                  [?a :assignment/by-day ?date]
                  [?a :assignment/work ?work]
                  [?a :assignment/resource ?r]
                  
                  [(tick.alpha.api/> ?date ?fluxod-last-date)] ;; fluxod last-date
                  [(tick.alpha.api/<= ?date  ?max-date)]
                  #_[?r :resource/fluxod-name ?fluxod-name]
                  ]
                db
                id
                workplan
                fluxod-last-date
                max-date))


          grouped-fluxod-timesheets
          (group-fluxod-timesheets fluxod-timesheets :by-week? by-week?)

          grouped-ms-timesheets
          (group-ms-timesheets ms-timesheets :by-week? by-week?)
          
          
          
          resource-ts (cond
                        (not (seq grouped-ms-timesheets) )
                        grouped-ms-timesheets

                        (same-cell?
                         (last grouped-fluxod-timesheets)
                         (first grouped-ms-timesheets)
                         :by-week? by-week?)

                        (merge-timesheets grouped-fluxod-timesheets grouped-ms-timesheets)
                        :else (concat grouped-fluxod-timesheets grouped-ms-timesheets))
          ]
      
      
      
      {:resource-ts/id id
       :resource-ts/timesheets resource-ts
       :resource-ts/start-date (-> env :query-params :pathom/context :resource-ts/start-date)
       :resource-ts/end-date (-> env :query-params :pathom/context :resource-ts/end-date)
       :resource-ts/name (d/q '[:find ?name .
                                :in $ ?id
                                :where
                                [?r :resource/id ?id]
                                [?r :resource/name ?name]] db id)})))

(pc/defresolver workplan  [{:keys [connection db] :as env} {:keys [workplan/id]}]
  {::pc/input #{:workplan/id}
   ::pc/output [:workplan/id {:workplan/resources-ts [:resource-ts/id]} :resource-ts/start-date :workplan/end]}
  (let [
        start (-> env :query-params :pathom/context :resource-ts/start-date)
        end (-> env :query-params :pathom/context :resource-ts/end-date)]
    (do
      {:workplan/id id
       :workplan/resources-ts
       (mapv #(assoc % :workplan/id id)
             (d/q '[:find ?rid
                    :keys resource-ts/id
                    :in $ ?id
                    :where
                    [?p :project/id ?id]
                    [?p :project/assignments ?pa]
                    [?pa :assignment/resource ?r]
                    [?r :resource/id ?rid]] db id))})))

(def resolvers  [workplan resource-ts min-max-date])



