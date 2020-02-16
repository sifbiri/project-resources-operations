(ns app.model.user
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [app.application :as a :refer [SPA]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
            [clojure.set :as set]
            [app.math :as math]
            [clojure.string :as str]
            [tick.alpha.api :as t]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))


(defmutation save-fluxod-name [{:keys [] :as params}]
  (remote [env] true))
