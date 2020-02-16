(ns app.server-components.middleware
  (:require
    [app.server-components.config :refer [config]]
    [app.server-components.pathom :refer [parser]]
    [mount.core :refer [defstate]]
    [tick.alpha.api :as t]
    [com.fulcrologic.fulcro.server.api-middleware :refer [handle-api-request
                                                          wrap-transit-params
                                                          wrap-transit-response]]
    [ring.middleware.defaults :refer [wrap-defaults]]
    [ring.middleware.multipart-params :refer [wrap-multipart-params]]

    [ring.middleware.gzip :refer [wrap-gzip]]
    [ring.util.response :refer [response file-response resource-response]]
    [ring.util.response :as resp]
    [hiccup.page :refer [html5]]
    ;[com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
    
    [taoensso.timbre :as log]))

(def ^:private not-found-handler
  (fn [req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "NOPE"}))


(defn wrap-api [handler uri]
  (fn [request]
    (if (= uri (:uri request))
      (handle-api-request
        (:transit-params request)
        (fn [tx] (parser {:ring/request request} tx)))
      (handler request))))

;; ================================================================================
;; Dynamically generated HTML. We do this so we can safely embed the CSRF token
;; in a js var for use by the client.
;; ================================================================================
(defn index [csrf-token]
  (log/debug "Serving index.html")
  (html5
    [:html {:lang "en"}
     [:head {:lang "en"}
      [:title "OpsPortal"]
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
      [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css"
              :rel  "stylesheet"}]
      [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
      [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
     [:body
      [:div#app]
      [:script {:src "js/main/main.js"}]]]))

;; ================================================================================
;; Workspaces can be accessed via shadow's http server on http://localhost:8023/workspaces.html
;; but that will not allow full-stack fulcro cards to talk to your server. This
;; page embeds the CSRF token, and is at `/wslive.html` on your server (i.e. port 3000).
;; ================================================================================
(defn wslive [csrf-token]
  (log/debug "Serving wslive.html")
  (html5
    [:html {:lang "en"}
     [:head {:lang "en"}
      [:title "devcards"]
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
      [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css"
              :rel  "stylesheet"}]
      [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
      [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
     [:body
      [:div#app]
      [:script {:src "workspaces/js/main.js"}]]]))

(defn wrap-html-routes [ring-handler]
  (fn [{:keys [uri anti-forgery-token] :as req}]
    (cond
      (#{"/" "/index.html"} uri)
      (-> (resp/response (index anti-forgery-token))
        (resp/content-type "text/html"))

      ;; See note above on the `wslive` function.
      (#{"/wslive.html"} uri)
      (-> (resp/response (wslive anti-forgery-token))
        (resp/content-type "text/html"))

      :else
      (ring-handler req))))

(defn file-response? [{:keys [body]}]
  (when (map? body)
    (let [values    (vals body)
          has-file? (some :file/source values)
          multiple? (> (count values) 1)]
      (when (and has-file? multiple?)
        (throw (ex-info "A resolver is trying to respond with a file, but there were multiple elements in the request!" {:response body})))
      has-file?)))

(defn wrap-file-response
  "Middleware that converts responses that indicate the response is a file. "
  {:arglists '([handler] [handler options])}
  [handler]
  (fn [request]
    (let [response (handler request)]
      (if (file-response? response)
        (let [{:file/keys [source mime-type name]} (some-> response :body vals first)]
          (if source
            (-> (resp/response source)
                (cond->
                    mime-type (resp/header "Content-Type" mime-type))
                (resp/header "Content-Disposition" (str "attachment; filename=\"" (or name "file") "\""))
                (resp/header "Last-Modified" (ring.util.time/format-date (new java.util.Date))))
            {:status 404}))
        response))))

(defstate middleware
  :start
  (let [defaults-config (:ring.middleware/defaults-config config)
        legal-origins   (get config :legal-origins #{"localhost"})]
    (-> not-found-handler
        (wrap-api "/api")
        (wrap-file-response)
        (file-upload/wrap-mutation-file-uploads {})
      wrap-transit-params
      wrap-transit-response
      wrap-multipart-params
      (wrap-html-routes)
      ;; If you want to set something like session store, you'd do it against
      ;; the defaults-config here (which comes from an EDN file, so it can't have
      ;; code initialized).
      ;; E.g. (wrap-defaults (assoc-in defaults-config [:session :store] (my-store)))
      (wrap-defaults defaults-config)
      wrap-gzip
      

      )))
