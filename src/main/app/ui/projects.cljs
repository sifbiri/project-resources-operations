(ns app.ui.projects
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]


   

   [com.fulcrologic.semantic-ui.elements.input.ui-input :refer [ui-input]]
   [com.fulcrologic.semantic-ui.elements.icon.ui-icon :refer [ui-icon]]
   [com.fulcrologic.semantic-ui.elements.loader.ui-loader :refer [ui-loader]]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
                                        ;["react-country-flags" :as Flag]
   [app.application :as a :refer [SPA]]
                                        ;["react-flags" :as Flag]
   [app.model.session :as session]
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

   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
   [com.fulcrologic.semantic-ui.elements.divider.ui-divider :refer [ui-divider]]

   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]

   [com.fulcrologic.semantic-ui.elements.list.ui-list-content :refer [ui-list-content]]
   ["semantic-ui-react/dist/commonjs/modules/Checkbox/Checkbox" :default Checkbox]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list :refer [ui-list]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]

   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]

   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :refer [ui-form-input]]

   

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




(defmutation change-route [{:keys [this target]}]
  (action [{:keys [state]}
           ]
          (js/console.log "ss" target)
          (dr/change-route this target)))


(defsc Project
  [this {:project/keys [id name]}]
  {:query [:project/id :project/name :project/start-date  :project/last-published-date :project/modified-date :project/finish-date]
   :ident :project/id})



(defsc ProjectPanel [this {:ui/keys      []
                           :project/keys  [ ]
                           :project-panel/keys [made-up name start-date modified-date last-published-date finish-date]
                           :as          props}]
  {:query           [:project-panel/made-up :project-panel/id :project-panel/start-date :project-panel/modified-date  :some-stuff :project-panel/last-published-date :project-panel/finish-date :project-panel/name :project-panel/project-lead :project-panel/functional-lead :project-panel/technical-lead]
   :ident   :project-panel/id
   :route-segment   ["project-panel" :project-panel/id]

   :will-enter (fn [app {:keys [project-panel/id]}]
                 (dr/route-deferred
                  [:project-panel/id id]
                  (fn []
                    (df/load! app [:project-panel/id id] ProjectPanel)
                    
                    (comp/transact! app [(dr/target-ready {:target [:project-panel/id id]})]))))

   #_#_:componentDidMount (fn [this]
                        (js/console.log "SEE?" (comp/props this))
                        )}

  (js/console.log "props" (comp/props this))
  (let []
    
    (ui-container {:style {:width "60%"}}
                 
                 (dom/h3 {:style {:textAlign "center"}} "Project" )

                 (ui-form {}
                          (ui-form-group {}
                                         (ui-form-field {:width 6}(ui-form-input {:label "Name" :placeholder "Project Name" :readOnly true :value name}))

                                        (ui-form-input  {:label "Modified Date" :type "datetime-local" :readOnly true
                                                          :value
                                                          (apply str (take 16 (str (t/date-time modified-date))))

                                                          :onChange
                                                          (fn [e d] (js/console.log "data" (.-value (.-target e)) "datein" modified-date ))})
                                         (ui-form-input  {:label "Published Date" :type "datetime-local" :readOnly true
                                                          :value
                                                            (apply str (take 16 (str (t/date-time last-published-date))))
                                                          
                                                          })
                                         
                                         
                                         
                                         
                                         )
                          (ui-form-group {} (ui-form-input  {:label "Start Date" :type "datetime-local" :readOnly true
                                                             :value
                                                             (apply str (take 16 (str (t/date-time start-date))))
                                                             
                                                             })
                                         (ui-form-input  {:label "End Date" :type "datetime-local" :readOnly true
                                                          :value
                                                          (apply str (take 16 (str (t/date-time finish-date))))
                                                          
                                                          }))
                          (ui-divider {})

                          (ui-form-group {} )))))




(defsc AdminProjects [this {:admin-projects/keys [projects] :as props}]
  {:query         [{:admin-projects/projects (comp/get-query Project)} [::uism/asm-id ::session/session]]
   :ident         (fn [] [:component/id :admin-projects])
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-projects]
                  (fn []
                    
                    (df/load! app :all-projects Project

                              {:target 
                               [:component/id :admin-projects :admin-projects/projects]
                               
                               })
                    (comp/transact! app [(dr/target-ready {:target [:component/id :admin-projects]})]))))
   :route-segment ["admin-projects"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
 
  
   }

  
  (let [current-state (uism/get-active-state this ::session/session)
        logged-in? (= :state/logged-in current-state)]
    
    (if logged-in?
      (ui-container {:style {:width "60%"}}
                    
                    (dom/h3 {:style {:textAlign "center"}} "Projects" )

                    (ui-table {} (ui-table-header {}
                                                  (ui-table-row {}
                                                                
                                                                (ui-table-header-cell {:style {:backgroundColor "#3281b9" :color "#ffffff" :position "sticky" :top 0}} "Project Name")))
                              (ui-table-body {}
                                             (map (fn [p] (ui-table-row {:onClick (fn [e]
                                                                                    (merge/merge-component! this  ProjectPanel {:project-panel/id (:project/id p)})
                                                                                    (dr/change-route this (dr/path-to ProjectPanel {:project-panel/id (:project/id p)  :some-stuff 1}) )

                                                                                    )}
                                                                        
                                                                 (ui-table-cell {} (:project/name p)))) projects)))
                    )
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))
