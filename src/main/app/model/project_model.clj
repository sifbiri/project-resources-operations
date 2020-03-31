(ns app.model.project-model
  (:require [clj-http.client :as client]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.json :as json]
            [datomic.api :as d]
            [app.model.database :refer [db-url]]
            [tinklj.config :refer [register]]
            [tinklj.keys.keyset-handle :as keyset-handles]
            [tinklj.encryption.aead :refer [encrypt decrypt]]
            [tinklj.keysets.keyset-storage  :as keyset-storage]
            [tinklj.primitives :as primitives]
            [app.model.session :as session]))

(register)

(def filename "resources/ks.json")
(def keyset-handle (keyset-handles/generate-new :aes128-gcm))

(keyset-storage/write-clear-text-keyset-handle keyset-handle filename)

;(println "hello")


(defn zip-str [s]
  (zip/xml-zip
   (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))
;; the goal is  to get cookies and then set them.

(def cs (clj-http.cookies/cookie-store))


                                        ;(client/get "http://google.com" {:cookie-store cs})

;; security token



                                        ; AssignmentTimephasedDataSet


(defn prepare-request-options []
  (let [{:keys [email salt password]
         } (first (d/q '[:find ?e ?s ?p
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

           pass (decrypt aead password (.getBytes salt))
           
        
        res1-security-token (client/post "https://login.microsoftonline.com/extSTS.srf"
                                         {:body (session/make-load email (String. pass)) :body-encoding "UTF-8"
                                          :cookie-store cs})


        security-token (-> (zip-str
                                (:body res1-security-token))
                           first :content second :content first :content (get 3) :content first :content first )


        res2-cookies (client/post "https://flu.sharepoint.com/_forms/default.aspx?wa=wsignin1.0"
                                      {:body security-token :body-encoding "UTF-8"
                                       :cookie-store cs})
        res3-request-digest (client/post  "https://flu.sharepoint.com/sites/pwa/_api/contextinfo"

                                          {:cookie-store cs})

        digest (-> (zip-str (:body res3-request-digest)) first :content second :content first)


        

        
        

        ]
    

    {;:async? true
     ;:user email
     ;:password (String. pass)
     :cookie-store cs ;:oauth-token security-token
     :headers {"X-RequestDigest" digest
               "If-Match" "*"}
     
     :accept :json
     }
    

    ))





;; ;; to get security token

;; (def res1-security-token (client/post "https://login.microsoftonline.com/extSTS.srf"
;;                                       {:body (clojure.java.io/file "src/load.xml") :body-encoding "UTF-8"
;;                                        :cookie-store cs}))

;; (def security-token (-> (zip-str
;;                          (:body res1-security-token))
;;                         first :content second :content first :content (get 3) :content first :content first ))
;; ;; to get cookies
                                   
;; (def res2-cookies (client/post "https://flu.sharepoint.com/_forms/default.aspx?wa=wsignin1.0"
;;                                {:body security-token :body-encoding "UTF-8"
;;                                 :cookie-store cs}))

;; ;; to get request digest


;; (def res3-request-digest (client/post  "https://flu.sharepoint.com/sites/pwa/_api/contextinfo"

;;                                        {:cookie-store cs}))


;; (def digest (-> (zip-str (:body res3-request-digest)) first :content second :content first))



;; ;; (client/post "https://flu.sharepoint.com/_api/web/lists"
;; ;;              {:body "{'AllowContentTypes': true, 'BaseTemplate': 100,
;; ;;                      'ContentTypesEnabled': true, 'Description': 'My list description', 'Title': 'Test' }"
;; ;;               :cookie-store cs
;; ;;               ;:oauth-token security-token
;; ;;               })


;; (def response (-> (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectServer/Projects"
;;                               (prepare-request-options))
;;                   :body
;;                   (json/read-str :key-fn keyword)
;;                   :value
;;                   )
;;   )
;; (def all-projects  (map #(select-keys % [:Name :Id]) response))




;; (-> (client/post
;;      "https://flu.sharepoint.com/sites/pwa/_api/contextInfo"
;;      {:Cookie-store cs ;:oauth-token security-token
;;       :headers {"X-RequestDigest" digest}
;;       :accept :json
;;       })
;;     :body
;;     (json/read-str :key-fn keyword))

;;{"Name":"Test 679"}





;; (def response (-> (client/post
;;                    "https://flu.sharepoint.com/sites/pwa/_api/ProjectServer/Projects('1747f6cc-bdfc-e911-b19d-9cb6d0e1bd60')/Draft/checkIn()"
;;                    {:cookie-store cs ;:oauth-token security-token
;;                     :headers {"X-RequestDigest" "0xB9225D70A7EDE140C5FC75FF569A8FDE4317CE40296C0A8EC9778BF27244345A63C7ABD074B9480B3AF22A0F2460E6B48129A684874F819622B97BB2133A7FB9,07 Nov 2019 14:54:59 -0000"}
;;                     :accept :json
;;          ;           :body "{\"Name\": \"TestStuff\"}"
;;                     })
;;                   :body
;;                   (json/read-str :key-fn keyword)))

;;access token // cookies



()


;; test

;; (client/get "https://flu.sharepoint.com/sites/pwa/_api/ProjectData"
;;             {:cookie-store cs})

;; (client/get "https:flu.sharepoint.com/_api/web/lists" {:cookie-store cs})



(comment
  (clojure.pprint/pprint (clj-http.cookies/get-cookies cs)))
