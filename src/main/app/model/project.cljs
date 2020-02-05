(ns app.model.project
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))



(defmutation get-or-create-current-gov-review-week [{:keys [gov-review-week/week]}]
                                        ;(action [env] )
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)]
            (-> env
                (m/returning GovReviewWeek)
                (m/with-target
                  (targeting/multiple-targets
                   (targeting/replace-at [:component/id :gov-review :gov-review/current-week])))))))


(defmutation submit-current-gov-review-week  [{:keys [gov-review-week] :as params}]
  (action [env]
          (js/console.log "TTTT" gov-review-week))
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)
                resource-id (get-in @state [:component/id :session :account/resource])]
            (-> env
                (m/with-params (merge params {:resource-id resource-id}))
                (m/returning GovReviewWeek)
                ))))


(defmutation get-or-create-gov-review-week [{:keys [gov-review-week/week index]}]
                                        ;(action [env] )
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)]
            (-> env
                (m/returning GovReviewWeek)
                (m/with-target
                  (targeting/replace-at [:component/id :gov-review :gov-review/current-weeks index]))))))




(defmutation populate-projects [_]
  (action [{:keys [state]}]
          (let [projects (get @state :projects)]
            (println "HI THERE"  projects)
            
            (swap! state assoc-in [:component/id :resources :resources/projects] projects)
            (swap! state assoc  :projects []))))



(defmutation set-project-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/project-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))



(defmutation set-project-status [{:keys [project-info/id status]}]

  (action [{:keys [state]}]
          (js/console.log "project panel id" id)
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/status] status)))))
  (remote [env] true))

(defmutation set-project-entity [{:keys [project-info/id entity]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/entity] entity)))))
  (remote [env] true))



(defmutation set-current-gov-week [{:keys [gov-review-week]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:component/id :gov-review :gov-review/current-week] [:gov-review-week/week (:gov-review-week/week gov-review-week)])))))
                                        ;(remote [env] true)
  )



(defmutation set-project-fluxod-name [{:keys [project-info/id name]}]
  (action [{:keys [state]}]
          (let [TeamCheckbox (comp/registry-key->class :app.ui.root/TeamCheckbox)]
            
            (swap! state (fn [state]
                           (-> state
                               (assoc-in [:project-info/id id :project-info/fluxod-name] name)
                               )))
            ;; (swap! state  merge/merge-component TeamCheckbox
            ;;        {:db/id team-id :team/name name }
            
            ;;        )
            ))
  (remote [env] true))






(defmutation set-project-phase [{:keys [project-info/id phase]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/phase] phase)))))
  (remote [env] true))


(defmutation set-functional-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/functional-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))

(defmutation set-technical-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state component app ref] :as env}]
                                        ;(ns/)
          (let [ProjectInfo (comp/registry-key->class :app.ui.projects/ProjectInfo)]
            

            (mutations/set-props {:project-info/new "NEW2"})
            (js/console.log "ERF" ref)
                                        ;(swap! state update-in ref (fn [st] (merge st {:project-info/new "NEW"})))
            ;(ns/update-caller! env (fn [m ](merge m {:project-info/new "NEW"})))
            (js/console.log "VAL 1" (comp/get-query ProjectInfo))
            (js/console.log "VAL 2" (denormalize/db->tree [:project-info/id {:project-info/technical-lead [:resource/name]} ] [:project-info/id  #uuid "850d9f1e-27e1-e911-b19b-9cb6d0e1bd60"] @state))

            (swap! state (fn [state]
                          (-> state
                              (assoc-in [:project-info/id id :project-info/technical-lead]
                                        [:resource/id  lead-id]))))))
  (remote [env] true))






