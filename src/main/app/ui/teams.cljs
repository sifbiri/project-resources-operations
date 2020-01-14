(ns app.ui.teams
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]


   [com.fulcrologic.fulcro.dom.inputs :as inputs]

   [com.fulcrologic.semantic-ui.elements.input.ui-input :refer [ui-input]]
   [com.fulcrologic.semantic-ui.elements.icon.ui-icon :refer [ui-icon]]
   [com.fulcrologic.semantic-ui.elements.loader.ui-loader :refer [ui-loader]]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
                                        ;["react-country-flags" :as Flag]
   [app.application :as a :refer [SPA]]
                                        ;["react-flags" :as Flag]
   [app.model.session :as session]
   [app.ui.users :as users]
   ["react-table" :as react-table]
   ["react-world-flags" :as Flag]
   ["react-flag-kit" :as FlagIcon ]
   ["react-sticky-table" :as StickyTable]
   [app.model.resource :as resource]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [app.model.work-line :as work-line]
   ["semantic-ui-react/dist/commonjs/modules/Dropdown/Dropdown" :default Dropdown]
   [clojure.set :as set]
   
   [clojure.string :as str]
   [app.math :as math]
   ["react-number-format" :as NumberFormat]
   ["react-country-flag" :as  ReactCountryFlag]
                                        ;["react-collapsing-table" :as ReactCollapsingTable]

                                        ;["react-calendar-timeline" :as TimeLine]
   ["semantic-ui-calendar-react" :as SemanticUICalendar]
   ["react-timeline-9000" :as ReactTimeLine]
   [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [tick.alpha.api :as t]
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.dom.events :as evt]
   [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
   [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
   [com.fulcrologic.semantic-ui.elements.flag.ui-flag :refer [ui-flag]]
   [app.model.item :as item]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]

   [com.fulcrologic.semantic-ui.collections.form.ui-form-checkbox :refer [ui-form-checkbox]]



   ;; semantic comoponents 
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion :refer [ui-accordion]]
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion-title :refer [ui-accordion-title]]
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion-content :refer [ui-accordion-content]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-checkbox :refer [ui-form-checkbox]]
   ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-group :refer [ui-form-group]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
   [com.fulcrologic.semantic-ui.elements.divider.ui-divider :refer [ui-divider]]

   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]

   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal :refer [ui-modal]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-content :refer [ui-modal-content]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-description :refer [ui-modal-description]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-header :refer [ui-modal-header]]
   

   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [cljs-time.core :as tt]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as tc]
   [taoensso.timbre :as log]


                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]





   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]

   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]

   ))


;; TODO make it remote






(defmutation add-team-member [{:keys [team-id team-member-id]}]
  (action [{:keys [state]}]
          (js/console.log "ID" team-member-id)
          (swap! state (fn [state]
                         (-> state
                             (update-in [:team/id team-id :team/resources] conj [:resource/id team-member-id] ))))))


(defmutation delete-team-member [{:keys [team-id team-member-id]}]
  (action [{:keys [state]}]
          (js/console.log "ID" team-member-id)
          (swap! state (fn [state]
                         (-> state
                             (merge/remove-ident* [:resource/id team-member-id] [:team/id team-id :team/resources]))))))


(defsc Team [this {:team/keys [name type lead resources] :keys [resource/options2 resource/options db/id] :as props}]
  {:query [:team/name :team/type
           :db/id
           {:team/lead (comp/get-query users/Resource)}
           {:team/resources (comp/get-query users/Resource)}
           [:resource/options2 '_]
           [:resource/options '_]]
   :ident (fn [] [:team/id (:db/id props)])
   

   }
  (js/console.log "options" (comp/props this))
  (let [filtered-resources (filterv #(not= (:resource/profile %) :profile/user) options2)]
    (js/console.log "RRR" )
    (ui-modal {:trigger (ui-table-row {:onClick (fn [e] (js/console.log "Clicked"))}
                                      (ui-table-cell {} name)
                                      (ui-table-cell {} (clojure.string/capitalize (apply str (rest (str type)))))
                                      (ui-table-cell {} (:resource/name lead))
                                      (ui-table-cell {} (count resources))) :size :small}
              
              (ui-modal-content {}

                                (ui-container {:style {:width "80%"}}
                                              (ui-form {}
                                                       (ui-form-group {}
                                                                      (ui-form-field {}
                                                                                     (dom/label {} "Team Name")
                                                                                     (ui-input {:placeholder "Team Name"
                                                                                                :onChange (fn [e d] (m/set-string! this :team/name :event e))
                                                                                                :value name}))
                                                                      (div {:style {:margin "20px"}})
                                                                      (ui-form-field {}
                                                                                     (dom/label {} "Type")
                                                                                     (ui-dropdown
                                                                                      {:placeholder "Team Name"
                                                                                       :onChange (fn [e d] (m/set-value! this :team/type (keyword (.-value d))))
                                                                                       :value type
                                                                                       :options [{:text "Organization" :value :organization}
                                                                                                 {:text "Project" :value :project}]}))

                                                                      )
                                                       (ui-form-group {}
                                                                      (ui-form-field {}
                                                                                     (dom/label {} "Team Lead")
                                                                                     (ui-dropdown {:placeholder "Team Lead"
                                                                                                   :options filtered-resources
                                                                                                   :value (:resource/id lead)
                                                                                                   :onChange #(m/set-value! this :team/lead [:resource/id (.-value %2)])})
                                                                                     
                                                                                     ))
                                                       (ui-form-group {}
                                                                      (ui-form-field {}
                                                                                     (dom/label {} "Add Member")
                                                                                     (ui-dropdown {:placeholder "Team Member" 
                                                                                                   :options (map #(select-keys % [:value :text]) options)                                                                                                   
                                                                                                   :selection true
                                                                                                   :onChange #(comp/transact! this [ (add-team-member {:team-member-id (.-value %2) :team-id id})])
                                                                                                   :search true
                                                                                                   :item true})
                                                                                     
                                                                                     ))
                                                       )
                                              (ui-table {:style {:fontSize "90%"
                                                                 :position "relative"
                                                                 
                                                                 
                                                                 }
                                                         :celled true
                                                         :striped true
                                                         :selectable true}
                                                        (ui-table-header {}
                                                                         (ui-table-row {} (map #(ui-table-header-cell {:style {:backgroundColor "#3281b9" :color "#ffffff" :position "sticky" :top 0}} %) [ "Name" "Action"])))
                                                        (ui-table-body {}
                                                                       (map #(ui-table-row {} (ui-table-cell {} (:resource/name %)) (ui-table-cell {} (ui-button {:onClick (fn [e](comp/transact! this  [(delete-team-member {:team-id id :team-member-id (:resource/id %)})]))} "Delete")) ) resources)
                                                                       ))
                                              )
                                
                                ))
    ))


(def ui-team (comp/factory Team {:keyfn :team/name}))


(defsc Teams [this {:teams/keys [teams]}]
  {:query         [{:teams/teams (comp/get-query Team)}]
   :ident         (fn [] [:component/id :admin-teams])
   :route-segment ["admin-teams"]
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-teams]
                  (fn []
                    (df/load! app :teams Team
                              {:target [:component/id :admin-teams :teams/teams]})
                    (comp/transact! app [(dr/target-ready {:target [:component/id :admin-teams]})]))))
   
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   
   }
  

  (ui-container {:style {:width "80%"}}
                (dom/h3 {:style {:textAlign "center"}} "Teams" )


                

                

                
                (ui-table {:style {:fontSize "90%"
                                   :position "relative"
                                   
                                   
                                   }
                           :celled true
                           :striped true
                           :selectable true}
                          (ui-table-header
                           {:fullWidth true :style {:position "sticky" :top 0}}
                           (ui-table-row
                            {:style {:backgroundColor "red"}}

                            (map #(ui-table-header-cell {:style {:backgroundColor "#3281b9" :color "#ffffff" :position "sticky" :top 0}} %) ["Team Name" "Team Type" "Lead" "Nb Resource"])

                            
                            ))
                          (js/console.log "TEAMS" teams)
                          (ui-table-body {}
                                         (map ui-team teams)))))





