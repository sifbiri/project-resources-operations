(ns app.server-components.pathom
  (:require
   [mount.core :refer [defstate]]
   [edn-query-language.core :as eql]
    [taoensso.timbre :as log]
    [com.wsscode.pathom.connect :as pc]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.common.async-clj :refer [let-chan]]
    [clojure.core.async :as async]
    [app.model.account :as acct]
    [app.model.user :as user]
    [app.model.work-line :as wol]
    ;; [app.model.project :as project]
    [com.wsscode.pathom.connect.datomic :as pcd]
    [com.wsscode.pathom.connect.datomic.on-prem :refer [on-prem-config]]
    [app.model.project :as project]
    [app.model.import :as import]
    [app.model.session :as session]
    [app.model.resource :as resource]
    [com.fluxym.components.auto-resolvers :refer [automatic-resolvers]]
    [app.model.item :as item]
    [app.model.team :as team]
    [datomic.api :as d]
    [com.fulcrologic.rad.database-adapters.datomic :as datomic]
    [app.model.database :refer [datomic-connections]]
    [com.fulcrologic.rad.form :as form]
    
    [app.server-components.config :refer [config]]
    [app.model.database :as db]))

(pc/defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (-> (get env ::pc/indexes)
     (update ::pc/index-resolvers #(into [] (map (fn [[k v]] [k (dissoc v ::pc/resolve)])) %))
     (update ::pc/index-mutations #(into [] (map (fn [[k v]] [k (dissoc v ::pc/mutate)])) %)))})

(def all-resolvers [automatic-resolvers form/save-form form/delete-entity project/resolvers acct/resolvers session/resolvers resource/resolvers  index-explorer wol/resolvers  item/resolvers team/resolvers import/resolvers user/resolvers])

(defn preprocess-parser-plugin
  "helper to create a plugin that can view/modify the env/tx of a top-level request.

  f - (fn [{:keys [env tx]}] {:env new-enbasv :tx new-tx})

  If the function returns no env or tx, then the parser will not be called (aborts the parse)"
  [f]
  {::p/wrap-parser
   (fn transform-parser-out-plugin-external [parser]
     (fn transform-parser-out-plugin-internal [env tx]
       (let [{:keys [env tx] :as req} (f {:env env :tx tx})]
         (if (and (map? env) (seq tx))
           (parser env tx)
           {}))))})

(defn log-requests [{:keys [env tx] :as req}]
  (log/debug "Pathom transaction:" (pr-str tx))
  req)

;; CODE from RAD 


(def query-params-to-env-plugin
  "Adds top-level load params to env, so nested parsing layers can see them."
  {::p/wrap-parser
   (fn [parser]
     (fn [env tx]
       (let [children     (-> tx eql/query->ast :children)
             query-params (reduce
                           (fn [qps {:keys [type params] :as x}]
                             (cond-> qps
                               (and (not= :call type) (seq params)) (merge params)))
                           {}
                           children)
             env          (assoc env :query-params query-params)]
         (parser env tx))))})

(defn build-parser [db-connection]
  (let [real-parser (p/parallel-parser
                      {::p/mutate  pc/mutate-async
                       ::p/env     {::p/reader               [p/map-reader pc/parallel-reader
                                                              pc/open-ident-reader p/env-placeholder-reader]
                                    ::p/process-error
                                    (fn [_ err]
                                        ; print stack trace
                                      (.printStackTrace err)

                                        ; return error str
                                      (p/error-str err))
                                    
                                    ::p/placeholder-prefixes #{">"}
                                    ::p/thread-pool (pc/create-thread-pool (async/chan 200))}
                       ::p/plugins [(pc/connect-plugin {::pc/register all-resolvers})
                                    ;(pcd/datomic-connect-plugin (assoc on-prem-config ::pcd/conn db-connection))
                                    (p/env-wrap-plugin (fn [env]
                                                         ;; Here is where you can dynamically add things to the resolver/mutation
                                                         ;; environment, like the server config, database connections, etc.
                                                         
                                                         (-> env

                                                             (datomic/add-datomic-env {:dev (:ops datomic-connections)})
                                                             (assoc 
                                                               :db (d/db db-connection) ; real datomic would use (d/db db-connection)
                                                               :connection db-connection
                                                               :config config))))
                                    (preprocess-parser-plugin log-requests)
                                    query-params-to-env-plugin
                                    p/error-handler-plugin
                                    p/request-cache-plugin
                                    (p/post-process-parser-plugin p/elide-not-found)
                                    p/trace-plugin]})
        ;; NOTE: Add -Dtrace to the server JVM to enable Fulcro Inspect query performance traces to the network tab.
        ;; Understand that this makes the network responses much larger and should not be used in production.
        trace?      (not (nil? (System/getProperty "trace")))]
    (fn wrapped-parser [env tx]
      (async/<!! (real-parser env (if trace?
                                    (conj tx :com.wsscode.pathom/trace)
                                    tx))))))

(defstate parser
  :start (build-parser db/conn))






