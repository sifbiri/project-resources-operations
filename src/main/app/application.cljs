(ns app.application
  (:require [com.fulcrologic.fulcro.networking.http-remote :as net]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.rendering.keyframe-render :as kr]
            [app.model.work-line :as work-line]))
                                        ;[app.ui.root :as root]))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
   (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
   (net/wrap-fulcro-request)))

(defonce SPA (app/fulcro-app
              {;; This ensures your client can talk to a CSRF-protected server.
               ;; See middleware.clj to see how the token is embedded into the HTML
               :remotes {:remote (net/fulcro-http-remote
                                  {:url                "/api"
                                   :request-middleware secured-request-middleware})}



               ;; :optimized-render! kr/render!
               :client-did-mount
               (fn [app]

                 (let [WorkLine (comp/registry-key->class :app.ui.root/WorkLine)
                                        ;                                       Category (comp/registry-key->class :app.ui.root/Category)
                       ]
                   (df/load app  :work-day/all-work-lines
                             WorkLine
                             {:target [:component/id :work-day :work-day/all-work-lines]}))
                 #_(df/load app :category/all-categories
                            Category
                            {:post-mutation `item/create-category-options}))}))

(comment
  (-> SPA (::app/runtime-atom) deref ::app/indexes))
