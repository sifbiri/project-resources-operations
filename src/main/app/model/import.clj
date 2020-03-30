(ns app.model.import
  (:require
   [tinklj.encryption.aead :refer [encrypt decrypt]]
   [tinklj.keys.keyset-handle :as keyset-handle]
   [tinklj.primitives :as primitives]
   [com.wsscode.pathom.connect :as pc]
   [clj-fuzzy.metrics :as fm]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [app.model.database :refer [conn]]
   [clojure.core.async :refer [go]]
   [clojure.set :as set]
   [app.model.api :as api]
   [dk.ative.docjure.spreadsheet :as s]
   [app.model.api :as api]
   [tick.alpha.api :as t]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [datomic.api :as d]
   [app.model.database :refer [db-url]]
   [taoensso.timbre :as log]
   [tinklj.keys.keyset-handle :as keyset-handles]
   [tinklj.keysets.keyset-storage  :as keyset-storage]
   [tinklj.primitives :as primitives]
   [tinklj.encryption.aead :refer [encrypt decrypt]]))

#_(def ciphertext (encrypt aead (.getBytes "password") aad))


(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))





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

(pc/defresolver msaccount [{:keys [connection db]} _]
  {::pc/output [{:msaccount [:msaccount/password :msaccount/email]}]}
  (let [{:keys [email password salt]}
        (first (d/q '[:find ?e ?s ?p
                      :keys email salt password
                      :where
                      [?d :msaccount/password ?p]
                      [?d :msaccount/email ?e]
                      [?d :msaccount/salt ?s]
                      [?d :msaccount/type :msaccount]
                      ]
                    (d/db (d/connect db-url))))
        keyset-handle (keyset-storage/load-clear-text-keyset-handle "resources/ks.json")
        aead (primitives/aead keyset-handle)
        
        pass (decrypt aead password  (.getBytes salt))]
    {:msaccount {:msaccount/password (String. pass) :msaccount/email email}}))


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
  (println "NAME n->" name)
  (d/q '[:find ?fn .
         :in $ ?name
         :where
         [?r :resource/name ?name]
         [?r :resource/fluxod-name ?fn]]
       db
       name))
  
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
  
  (with-open [stream (clojure.java.io/input-stream (:tempfile (first files)))]
    
    (let [s (:tempfile (first files))
          filename (:filename (first files))
          fluxod-names
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
                                                                                  :in $ ?start ?end
                                                                                  :where
                                                                                  [?e :fluxod-ts/date ?date]
                                                                                  [(tick.alpha.api/>= ?date ?start)]
                                                                                  [(tick.alpha.api/<= ?date ?end)]
                                                                                  ] db
                                                                                    (:import/start-period new-import)
                                                                                    (:import/end-period new-import)))))
      
      (d/transact connection
                  (concat tx-fluxod-ts tx-fluxod-names))
      ;; import history 
      (d/transact connection [(-> new-import (assoc :import/files files2  :db/id "NEW_ID"))])
      (try
        (with-open [st (io/input-stream s)]
            (io/copy s (io/file (str "resources/imports/" filename ))))
        (catch Exception e
          (println "Catched and message: " (.getMessage e)))) 
      ))
  {:a 1})

(pc/defmutation
  get-import-file
  [{:keys [env connection] :as env} {:keys [filename]}]
  {::pc/input [:filename]
   ::pc/output [:file/mime-type :file/name :file/source]}
  (let [f (clojure.java.io/file (str "resources/imports/" filename))]
    {:file/mime-type "application/xls"
     :file/name      filename
     :file/source    f}))

(pc/defmutation
  update-db
  [{:keys [env connection] :as env} {:keys []}]
  {::pc/input []
   ::pc/output []}
  (do
    (api/update-db)
    {}))


(pc/defmutation
  save-msaccount
  [{:keys [db connection] :as env}  {:keys [email password]}]
  {::pc/params [:email :passowrd]
   ::pc/output []}
  (let [keyset-handle (keyset-storage/load-clear-text-keyset-handle "resources/ks.json")
        aead (primitives/aead keyset-handle)
        aad (rand-str (+ (rand-int 12) 2))
        aadbytes (.getBytes aad)
        ciphertext (encrypt aead (.getBytes password) aadbytes)
        ]

    (if (d/pull db [:msaccount/email] [:msaccount/type :msaccount])
      (d/transact connection [{:db/id [:msaccount/type :msaccount]
                               :msaccount/email email  :msaccount/salt aad :msaccount/password ciphertext}])

      (d/transact connection [{:db/id "new" :msaccount/email email   :msaccount/salt aad :msaccount/password ciphertext :msaccount/type :msaccount}]))
    {}
    ))

(def resolvers [save-msaccount import-file name->fluxod-name all-imports import get-import-file update-db msaccount])












