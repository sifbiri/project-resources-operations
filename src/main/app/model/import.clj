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

(pc/defmutation
  import-file
  [{:keys [:connection :db] :as env} {::file-upload/keys [files]}]
  {
   ::pc/params [::file-upload/files]
   ::pc/output []
   }
  (println "FILES" files)
  (do
    (with-open [stream (clojure.java.io/input-stream (:tempfile (first files)))]
      (let[ fluxod-names
           (->> (s/load-workbook stream)
                (s/select-sheet "Export des temps")
                (s/select-columns {:A :name})
                (map :name)
                distinct
                vec)
           ms-names
           (d/q '[:find ?name ?r 
                  :keys resource/name db/id 
                  :where
                  [?r :resource/name ?name]]
                db)
           tx
           (remove nil? (reduce (fn [r fluxod-name]
                                  (let [ 
                                        res (some #(when (str-fliped? fluxod-name (:resource/name %)) (assoc % :resource/fluxod-name fluxod-name )) ms-names)]
                                    
                                    (conj r res)
                                    #_(conj r (assoc (first ms-names-f)  :resource/fluxod-name fluxod-name))
                                    ))
                                []
                                fluxod-names))]
        
        ;(reset! ms-names-atom ms-names)
        ;(reset! fluxod-names-atom fluxod-names)
        @(d/transact connection
                     tx)))))






(defn n->fluxod-name [{:keys [connection db]} name]
  (d/q '[:find ?fn .
         :in $ ?name
         :where
         [?r :resource/name ?name]
         [?r :resource/fluxod-name ?fn]]))
  
(def name->fluxod-name
  (pc/single-attr-resolver :resource/name :resource/fluxod-name  n->fluxod-name))


(def resolvers [import-file name->fluxod-name])






#_(let [ fluxod-names
      (->> (s/load-workbook "resources/cra_du_2020-01-01_au_2020-01-31.xls")
           (s/select-sheet "Export des temps")
           (s/select-columns {:A :name})
           (map :name)
           distinct
           vec)
      ms-names
      (d/q '[:find ?name ?r 
             :keys resource/name db/id 
             :where
             [?r :resource/name ?name]]
           (d/db (d/connect db-url)))
      tx
        (vec (vec (reduce (fn [r fluxod-name]
                            (let [ms-names-f  (filter (fn [{:resource/keys [name]}]
                                                        (str-fliped? name fluxod-name)) ms-names)]
                              (println ms-names-f)
                              (when (seq ms-names-f) (conj r (assoc (first ms-names-f)  :resource/fluxod-name fluxod-name)))))
                          []
                          fluxod-names)))
      ]
  @(d/transact (d/connect db-url ) tx))

(comment
  (def fluxod-names
           (->> (s/load-workbook "resources/cra_du_2020-01-01_au_2020-01-31.xls")
                (s/select-sheet "Export des temps")
                (s/select-columns {:A :name})
                (map :name)
                distinct
                vec))
         (def ms-names
           (d/q '[:find ?name ?r 
                  :keys resource/name db/id 
                  :where
                  [?r :resource/name ?name]]
                (d/db (d/connect db-url))))

         tx
         (remove nil? (reduce (fn [r fluxod-name]
                                (let [ 
                                      res (some #(when (str-fliped? fluxod-name (:resource/name %)) (assoc % :resource/fluxod-name fluxod-name )) ms-names)]
                                  
                                  (conj r res)
                                  #_(conj r (assoc (first ms-names-f)  :resource/fluxod-name fluxod-name))
                                  ))
                              []
                              fluxod-names)))








