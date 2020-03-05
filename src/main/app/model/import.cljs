(ns app.model.import
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.networking.file-url :as file-url]

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
            [com.fulcrologic.fulcro.networking.http-remote :as http-remote]

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


(defmutation set-new-import-type [{:keys [:val]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import ] assoc :import/type val)))

(defmutation update-db [{:keys [:val]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import ] assoc :import/type val))
  (remote
   [env]
   true)
  (progress-action
   [{:keys [state ref] :as env}]
   (swap! state assoc-in [:component/id :imports :ui/progress]
          (http-remote/overall-progress env) ))
  (ok-action
   [env]
   (js/alert "Update finished")))


(defmutation set-import-files [{:keys [:files]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import] assoc :import/files files)))

(defmutation set-start-period [{:keys [:import/start-period]}]
  (action [{:keys [state ref] :as env}]
          (ns/update-caller-in! env [:imports/new-import ] assoc :import/start-period start-period)))


(defmutation set-end-period [{:keys [:import/end-period]}]
  (action [{:keys [state ref] :as env}]
          
          (ns/update-caller-in! env [:imports/new-import] assoc :end/start-period end-period)))

(defmutation get-import-file [{:keys [filename]}]
  (result-action [{:keys [state result ref]}]
                 ;; body will be a js ArrayBuffer. file-url ns has helpers to convert to data url
                 (let [file-url (file-url/raw-response->file-url result "application/xls")]
                   (swap! state assoc-in (conj ref :import/fileUrl)
                          file-url)
                   (file-url/save-file-url-as!
                    file-url
                    (get-in @state (conj ref :import/files 0 :file/name)))))
  (remote [env]
          (m/with-response-type env :array-buffer)))


(defmutation import-file [params]
  (action [{:keys [state ref] :as env}]
          (let [Import (comp/registry-key->class :app.ui.imports/Import) ]
            (ns/swap!-> state
                        (assoc-in (ns/tree-path->db-path @state [:component/id :imports :imports/new-import :import/time]) (t/inst)))
            ;(js/console.log "S"(get-in @state [:component/id :imports :imports/new-import]))
            (ns/swap!-> state
                        (targeting/integrate-ident*
                         (get-in @state [:component/id :imports :imports/new-import])
                         :append [:component/id :imports :imports/imports])
                        #_(merge/merge-component Import {:import/id (random-uuid)
                                                       :import/type :fluxod-timesheet
                                                       :import/start-period nil
                                                       :import/end-period nil}))))
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

