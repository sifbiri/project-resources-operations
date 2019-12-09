(ns app.model.session
  (:require
    [app.model.database :as db]
    [datascript.core :as d]
    [ghostwheel.core :refer [>defn => | ?]]
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [taoensso.timbre :as log]
    [clojure.spec.alpha :as s]
    [com.fulcrologic.fulcro.server.api-middleware :as fmw]
    [clj-http.client :as client]))

(defonce account-database (atom {}))


(def rest-xml " </o:UsernameToken>\n    </o:Security>\n  </s:Header>\n  <s:Body>\n    <t:RequestSecurityToken xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">\n      <wsp:AppliesTo xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">\n        <a:EndpointReference>\n          <a:Address>https://flu.sharepoint.com</a:Address>\n        </a:EndpointReference>\n      </wsp:AppliesTo>\n      <t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType>\n      <t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType>\n      <t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType>\n    </t:RequestSecurityToken>\n  </s:Body>\n</s:Envelope>\n")


(def top-xml "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"\n      xmlns:a=\"http://www.w3.org/2005/08/addressing\"\n      xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n  <s:Header>\n    <a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>\n    <a:ReplyTo>\n      <a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>\n    </a:ReplyTo>\n    <a:To s:mustUnderstand=\"1\">https://login.microsoftonline.com/extSTS.srf</a:To>\n    <o:Security s:mustUnderstand=\"1\"\n       xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n      <o:UsernameToken>\n ")

(defn make-load [username password]
  (str top-xml  "<o:Username>" username "</o:Username>\n        <o:Password>" password "</o:Password>\n" rest-xml))

;; post 
#_(client/post "https://login.microsoftonline.com/extSTS.srf"
               {:body (make-load "sbiri@flxym.com" "highAndAbove5$") :body-encoding "UTF-8"
                })

(defn valid-response?
  [r]
  (clojure.string/includes? (:body r) "BinarySecurityToken"))




(defresolver current-session-resolver [env input]
  {::pc/output [{::current-session [:session/valid? :account/name]}]}
  (let [{:keys [account/name session/valid?]} (get-in env [:ring/request :session])]
    (if valid?
      (do
        (log/info name "already logged in!")
        {::current-session {:session/valid? true :account/name name}})
      {::current-session {:session/valid? false}})))

(defn response-updating-session
  "Uses `mutation-response` as the actual return value for a mutation, but also stores the data into the (cookie-based) session."
  [mutation-env mutation-response]
  (let [existing-session (some-> mutation-env :ring/request :session)]
    (fmw/augment-response
      mutation-response
      (fn [resp]
        (let [new-session (merge existing-session mutation-response)]
          (assoc resp :session new-session))))))

(defmutation login [env {:keys [username password]}]
  {::pc/output [:session/valid? :account/name]}
  (log/info "Authenticating" username)
  (let [{expected-email    :email
         expected-password :password} (get @account-database username)]
    (if (and (= username expected-email) (= password expected-password))
      (response-updating-session env
                                 {:session/valid? true
                                  :account/name   username})
      (do
        (log/error "Invalid credentials supplied for" username)
        (throw (ex-info "Invalid credentials" {:username username}))))))




(defmutation login [env {:keys [username password]}]
  {::pc/output [:session/valid? :account/name]}
  (log/info "Authenticating" username)
  (let [response (client/post "https://login.microsoftonline.com/extSTS.srf"
                              {:body (make-load username password)
                               :body-encoding "UTF-8"})]
    (if (valid-response? response)
      (response-updating-session env
                                 {:session/valid? true
                                  :account/name   username})
      (do
        (log/error "Invalid credentials supplied for" username)
        (throw (ex-info "Invalid credentials" {:username username}))))))








(defmutation logout [env params]
  {::pc/output [:session/valid?]}
  (response-updating-session env {:session/valid? false :account/name ""}))

(defmutation signup! [env {:keys [email password]}]
  {::pc/output [:signup/result]}
  (swap! account-database assoc email {:email    email
                                       :password password})
  {:signup/result "OK"})

(def resolvers [current-session-resolver login logout signup!])
