(ns app.server-components.pathom
  (:require
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]
    [com.wsscode.pathom.connect :as pc]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.common.async-clj :refer [let-chan]]
    [clojure.core.async :as async]
    [app.model.account :as acct]
    [app.model.work-line :as wol]
    ;; [app.model.project :as project]
    [com.wsscode.pathom.connect.datomic :as pcd]
    [com.wsscode.pathom.connect.datomic.on-prem :refer [on-prem-config]]
    [app.model.project :as project]
    [app.model.session :as session]
    [app.model.resource :as resource]
    [app.model.item :as item]
    [app.model.team :as team]
    [datomic.api :as d]
    [app.server-components.config :refer [config]]
    [app.model.database :as db]))

(pc/defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (-> (get env ::pc/indexes)
     (update ::pc/index-resolvers #(into [] (map (fn [[k v]] [k (dissoc v ::pc/resolve)])) %))
     (update ::pc/index-mutations #(into [] (map (fn [[k v]] [k (dissoc v ::pc/mutate)])) %)))})

(def all-resolvers [acct/resolvers session/resolvers resource/resolvers project/resolvers index-explorer wol/resolvers #_project/resolvers item/resolvers team/resolvers])

(defn preprocess-parser-plugin
  "helper to create a plugin that can view/modify the env/tx of a top-level request.

  f - (fn [{:keys [env tx]}] {:env new-env :tx new-tx})

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

(defn build-parser [db-connection]
  (let [real-parser (p/parallel-parser
                      {::p/mutate  pc/mutate-async
                       ::p/env     {::p/reader               [p/map-reader pc/parallel-reader
                                                              pc/open-ident-reader p/env-placeholder-reader]
                                    ::p/placeholder-prefixes #{">"}}
                       ::p/plugins [(pc/connect-plugin {::pc/register all-resolvers})
                                    (pcd/datomic-connect-plugin (assoc on-prem-config ::pcd/conn db-connection))
                                    (p/env-wrap-plugin (fn [env]
                                                         ;; Here is where you can dynamically add things to the resolver/mutation
                                                         ;; environment, like the server config, database connections, etc.
                                                         (assoc env
                                                           :db (d/db db-connection) ; real datomic would use (d/db db-connection)
                                                           :connection db-connection
                                                           :config config)))
                                    (preprocess-parser-plugin log-requests)
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



