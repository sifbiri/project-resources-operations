(ns com.fluxym.model
  (:require
   [com.fluxym.model.account :as account]
   ;[com.example.model.item :as item]
   ;[com.example.model.invoice :as invoice]
   ;; [com.example.model.line-item :as line-item] ;
   ;; [com.example.model.address :as address]
                                        ;
   [com.fulcrologic.rad.attributes :as attr]))

(def all-attributes (vec (concat
                          account/attributes
                          ;; address/attributes
                          ;; item/attributes
                          ;; invoice/attributes
                          ;; line-item/attributes
                          ))) ;
