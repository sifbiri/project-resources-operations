(ns app.model.import
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




(def ms-names-atom (atom []))
(def fluxod-names-atom (atom []))

(defn str-fliped? [s1 s2]
  
  (let [split1 (str/split (str/trim s1) #" ")
        split2 (str/split (str/trim s2) #" ")]
    
    (try (and (= (str/lower-case (first split1)) (str/lower-case (second split2)))
              (= (str/lower-case (second split1)) (str/lower-case (first split2))))
         (catch Exception e
           false))))


(pc/defresolver all-imports [{:keys [connection db]} {:keys []}]
  {::pc/output [{:all-imports [:import/id]}]}
  {:all-imports (d/q '[:find ?id
                       :keys import/id 
                       :where
                       [?e :import/id ?id]] db)})


(pc/defresolver import  [{:keys [connection db]} {:keys [import/id]}]
  {::pc/input #{:import/id}
   ::pc/output [:import/id :import/type :import/start-period :import/end-period :import/files :import/time]}
  (d/pull db [:import/id :import/type :import/start-period :import/end-period :import/time {:import/files [:file/name]}] [:import/id id] ))



(def status-str->key {"Approuvé" :approved})
(def activity-type-str->key {"Congés (payés, sans solde)" :leave
                             "Mission" :mission
                             "Administratif / réunions internes" :meetings
                             "Formation" :training
                             "Développement / Projet interne" :internal-project
                             "Support client" :client-support
                             "Avant-vente" :pre-sale})
(defn n->fluxod-name [{:keys [connection db]} name]
  (d/q '[:find ?fn .
         :in $ ?name
         :where
         [?r :resource/name ?name]
         [?r :resource/fluxod-name ?fn]]))
  
(def name->fluxod-name
  (pc/single-attr-resolver2 :resource/name :resource/fluxod-name  n->fluxod-name))




(defn prepare-fluxod-ts-tx
  [file]
  (with-open [stream (clojure.java.io/input-stream file)]
    (->> (s/load-workbook stream)
        (s/select-sheet "Export des temps")
        (s/select-columns 
         {:A :fluxod-ts/resource-name :B :fluxod-ts/resource-entity
          :C :fluxod-ts/activity-type :D :fluxod-ts/client :E :fluxod-ts/po :F :fluxod-ts/entity-order
          :G :fluxod-ts/date
          :H :fluxod-ts/days
          :J :fluxod-ts/domain
          :K :fluxod-ts/comments
          :L :fluxod-ts/status
          :M :fluxod-ts/billable?
          :N :fluxod-ts/bill-ref
          :O :fluxod-ts/bill-date})
        (mapv (fn [{:keys [fluxod-ts/activity-type] :as m}]  (assoc m :fluxod-ts/activity-type (activity-type-str->key activity-type))))
        (mapv (fn [{:keys [fluxod-ts/status] :as m}]  (assoc m :fluxod-ts/status  (status-str->key status))))
        (mapv (fn [{:keys [fluxod-ts/billable?] :as m}]  (assoc m :fluxod-ts/billable? (if (= billable? "Non") false true))))
        (mapv (fn [m] (api/remove-nils m)))
        
        rest

        vec
        
        )))

(pc/defmutation
  import-file
  [{:keys [:connection :db] :as env} {::file-upload/keys [files] :keys [new-import]}]
  {
   ::pc/params [::file-upload/files :new-import]
   ::pc/output [:a]
   }
  
  (do

    (with-open [stream (clojure.java.io/input-stream (:tempfile (first files)))]
      (let [ fluxod-names
           (->> (s/load-workbook stream)
                (s/select-sheet "Export des temps")
                (s/select-columns {:A :name})
                (map :name)
                distinct
                vec)
            
            tx-fluxod-ts (prepare-fluxod-ts-tx (:tempfile (first files)))
           ms-names
           (d/q '[:find ?name ?r 
                  :keys resource/name db/id 
                  :where
                  [?r :resource/name ?name]]
                db)

           files (:import/files new-import)
           files2 (mapv #(assoc (select-keys % [:file/name]) :db/id "new" ) files)
           tx-fluxod-names
            (vec (remove nil? (reduce (fn [r fluxod-name]
                                    (let [ 
                                          res (some #(when (str-fliped? fluxod-name (:resource/name %)) (assoc % :resource/fluxod-name fluxod-name )) ms-names)]
                                      
                                      (conj r res)
                                      #_(conj r (assoc (first ms-names-f)  :resource/fluxod-name fluxod-name))
                                      ))
                                  []
                                  fluxod-names)))
           ]
        

        (d/transact connection (into [] (mapv (fn [e] [:db/retractEntity e]) (d/q '[:find [?e ...]
                                                                                            :where [?e :fluxod-ts/date _]] db))))
        (d/transact connection
                     (concat tx-fluxod-ts tx-fluxod-names))
        ;; import history 
        (d/transact connection [(-> new-import (assoc :import/files files2  :db/id "NEW_ID"))]))))
  {:a 1})

(def resolvers [import-file name->fluxod-name all-imports import])












