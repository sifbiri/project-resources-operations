(ns app.application
  (:require [com.fulcrologic.fulcro.networking.http-remote :as net]
            [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.rendering.multiple-roots-renderer :as mroot]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.data-fetch :as df]
            ;[app.ui.teams :as teams]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.rendering.keyframe-render2 :as kr]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.wsscode.pathom.core :as p]
            [app.model.work-line :as work-line]))
                                        ;[app.ui.root :as root]))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
   (net/wrap-fulcro-request)
   (file-upload/wrap-file-upload)
   (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))))



(defn contains-error?
  "Check to see if the response contains Pathom error indicators."
  [body]
  (when (map? body)
    (let [values (vals body)]
      (reduce
       (fn [error? v]
         (if (or
              (and (map? v) (contains? (set (keys v)) ::p/reader-error))
              (= v ::p/reader-error))
           (reduced true)
           error?))
       false
       values))))



(defonce SPA (app/fulcro-app
              {;; This ensures your client can talk to a CSRF-protected server.
               ;; See middleware.clj to see how the token is embedded into the HTML
               :remotes {:remote (net/fulcro-http-remote
                                  {:url                "/api"
                                   :request-middleware secured-request-middleware})}

               :remote-error?

               (fn [{:keys [body] :as result}]
                 (or
                  (app/default-remote-error? result)
                  #_(contains-error? body)))

               
               
               #_(fn [{:keys [status-code body]}]
                                   (or
                                    #_(has-reader-error? body)
                                    (not= 200 status-code)))

               :optimized-render!   mroot/render!
               :client-did-mount
               (fn [app]
                 (let [WorkLine (comp/registry-key->class :app.ui.root/WorkLine)
                       Project (comp/registry-key->class :app.ui.root/Project)
                       Resource (comp/registry-key->class :app.ui.root/Resource)
                       Team (comp/registry-key->class :app.ui.teams/Team)]
                    
                   ))}))

                   ;; (df/load app :work-line/all-projects
                   ;;          Project
                   ;;          {:post-mutation `work-line/create-project-options}
                   ;;          )


                   
                   
                   
                            
                   
                   

(comment
  (-> SPA (::app/runtime-atom) deref ::app/indexes))
