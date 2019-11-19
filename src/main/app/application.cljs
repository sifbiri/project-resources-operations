(ns app.application
  (:require [com.fulcrologic.fulcro.networking.http-remote :as net]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.components :as comp]
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

               :client-did-mount (let [ItemListItem (comp/registry-key->class :app.ui.root/ItemListItem)
                                       Category (comp/registry-key->class :app.ui.root/Category)]

                                   (fn [app]

                                     (fn [app]
                                       (df/load app :item/all-items
                                                ItemListItem
                                                {:target [:component/id ::item-list :item-list/all-items]})
                                       (df/load app :category/all-categories
                                                Category
                                                {:post-mutation `item/create-category-options}))

                                     
                                     ))
               }))



(comment
  (-> SPA (::app/runtime-atom) deref ::app/indexes))
