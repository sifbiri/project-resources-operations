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
   
   [datomic.api :as d]
   [app.model.database :refer [db-url]]
   [taoensso.timbre :as log]))




(def group-by-month 
  #(apply str (take 7 (str (t/date (:date %))))))







(defn same-month? [fluxod-ts ms-ts]
  (let [fluxod-last (-> fluxod-ts :timesheet/end-fluxod)
        ms-last (-> ms-ts :timesheet/start-ms )]
    (println "FLUXOD-ts" fluxod-last)
    (println "ms-ts" ms-last)
    (and fluxod-last ms-last (= (-> fluxod-last t/date t/month ) (-> ms-last t/date t/month)))))

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

  (do
    (let [fluxod-timesheets (sort-by
                             :date
                             (d/q '[:find ?work-fluxod ?date ?fluxod-po
                                    :keys timesheet/work-fluxod date fluxod-po
                                    :in $ ?rid ?pid ?min-date ?max-date
                                    :where
                                    [?r :resource/id ?rid]
                                    [?r :resource/name ?rn]
                                    
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
                                      id
                                      workplan
                                      min-date
                                      max-date))
          fluxod-last-date (or (-> fluxod-timesheets last :date) min-date)

          ms-timesheets
          (:sorty-by
           :date
           (d/q '[:find ?work ?date
                  :keys timesheet/work-ms date 
                  :in $ ?rid ?pid ?fluxod-last-date ?max-date
                  :where
                  [?r :resource/id ?rid]
                  [?r :resource/name ?rn]
                  
                  
                  [?pinfo :project-info/fluxod-client-name ?client]
                  [?pinfo :project-info/fluxod-project-names ?fluxod-po]
                  [?pinfo :project-info/id ?pid]
                  
                  [?e :project/id ?pid]
                  [?e :project/assignments ?a]
                  [?a :assignment/by-day ?date]
                  [?a :assignment/work ?work]
                  
                  [(tick.alpha.api/>= ?date ?fluxod-last-date)] ;; fluxod last-date
                  [(tick.alpha.api/<= ?date  ?max-date)]
                  [?r :resource/fluxod-name ?fluxod-name]]
                db
                id
                workplan
                fluxod-last-date
                max-date))


          fluxod-timesheets-by-month
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

                      [] (group-by group-by-month fluxod-timesheets)))

          ms-timesheets-by-month
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

                      [] (group-by group-by-month ms-timesheets)))
          
          resource-ts (cond
                        (not (seq fluxod-timesheets-by-month) )
                        ms-timesheets-by-month
                        (same-month?
                         (last fluxod-timesheets-by-month)
                         (first ms-timesheets-by-month))

                        (merge-timesheets fluxod-timesheets-by-month ms-timesheets-by-month)
                        :else (concat fluxod-timesheets-by-month ms-timesheets-by-month))
          ]
      (println "MS_TIMESHEETS" ms-timesheets-by-month)
      (println "Fluxod_TIMESHEETS" fluxod-timesheets-by-month)
      
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



