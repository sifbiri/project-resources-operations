(ns app.model.import
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [app.application :as a :refer [SPA]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
            [clojure.set :as set]
            [app.math :as math]
            [clojure.string :as str]
            [tick.alpha.api :as t]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))


(defn period-to-string [x]
  (->> x
       t/zoned-date-time
       (.toInstant)
       str
       (take 10)
       (str/join "")))


(defmutation set-import-files [{:keys [:files]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import ] assoc :import/files files)))

(defmutation set-start-period [{:keys [:import/start-period]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import ] assoc :import/start-period start-period)))


(defmutation set-end-period [{:keys [:import/end-period]}]
  (action [{:keys [state ref] :as env}]
          
          (ns/update-caller-in! env [:imports/new-import] assoc :end/start-period end-period)))


(defmutation import-file [params]
  (action [{:keys [state ref] :as env}]
          (let [Import (comp/registry-key->class :app.ui.imports/Import) ]
            (ns/swap!-> state
                        (assoc-in (ns/tree-path->db-path @state [:component/id :imports :imports/new-import :import/time]) (t/now)))
            ;(js/console.log "S"(get-in @state [:component/id :imports :imports/new-import]))
            (ns/swap!-> state
                        (targeting/integrate-ident*
                         (get-in @state [:component/id :imports :imports/new-import])
                         :append [:component/id :imports :imports/imports])
                        (merge/merge-component Import {:import/id (random-uuid)
                                                         :import/type :fluxod-timesheet
                                                         :import/start-period nil
                                                         :import/end-period nil
                                                         :import/status :new}))))
  (remote [env] true))

(defmutation produce-dates-from-file [{:keys [file/name]}]
  (action
   [{:keys [state ref] :as env}]
   (let [
         splited (str/split name #"_")
         start-period (-> (splited 2) js/Date.)
         end-period (->> (splited 4 ) (drop-last 4 ) (apply str) js/Date.)]
     (ns/update-caller-in! env
                           [:imports/new-import] assoc :import/start-period start-period)
     (ns/update-caller-in! env
                           [:imports/new-import] assoc :import/end-period end-period))))

