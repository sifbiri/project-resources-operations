(ns app.model.user
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


(pc/defmutation
  save-fluxod-name
  [{:keys [connection db] :as env} {:resource/keys [name id] :as resource-name}]
  {
   ::pc/params [:upload]
   ::pc/output [:upload]
   }
  @(d/transact connection [resource-name])
  {})


(def resolvers [save-fluxod-name])
