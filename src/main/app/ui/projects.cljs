(ns app.ui.projects
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]


   [app.ui.users :as users]

   [app.model.project :as project]

   [com.fulcrologic.semantic-ui.elements.input.ui-input :refer [ui-input]]
   [com.fulcrologic.semantic-ui.elements.icon.ui-icon :refer [ui-icon]]
   [com.fulcrologic.semantic-ui.elements.loader.ui-loader :refer [ui-loader]]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
                                        ;["react-country-flags" :as Flag]
   [app.application :as a :refer [SPA]]
                                        ;["react-flags" :as Flag]
   [app.model.session :as session]
   ["react-table" :as react-table]
   ["pure-react-carousel" :as pure-react-carousel]
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
   [com.fulcrologic.semantic-ui.modules.tab.ui-tab :refer [ui-tab]]
   [com.fulcrologic.semantic-ui.modules.tab.ui-tab-pane :refer [ui-tab-pane]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
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


   [com.fulcrologic.semantic-ui.elements.step.ui-step :refer [ui-step]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-content :refer [ui-step-content]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-description :refer [ui-step-description]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-group :refer [ui-step-group]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-title :refer [ui-step-title]]

   

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


(defsc TestC [this props]
  {:query []}
  (js/console.log "HI")
  (dom/p {} "Welcome :)"))

(def ui-test-c (comp/factory TestC ))
(defn test-c-func [props]
  [(dom/p {} "paragrah")
   (ui-test-c {})])


(defn ui-project-governance-review [props]
     
  (ui-tab-pane {}
               (ui-container {:textAlign "center"}
                 )))

;(def ui-project-governance-review (comp/factory ProjectGovernanceReview))

(defmutation change-route [{:keys [this target]}]
  (action [{:keys [state]}
           ]
          (js/console.log "ss" target)
          (dr/change-route this target)))


(defsc Project
  [this {:project/keys [id name]}]
  {:query [:project/id :project/name :project/start-date  :project/last-published-date :project/modified-date :project/finish-date]
   :ident :project/id})







(defsc ResourceQ
  [this {:resource/keys [id name email-address]}]
  {:query [:resource/id :resource/name :resource/email-address]
   }
  


                                        ;)

  
  
  )

;(declare ui-project-panel-router)



(def ui-carousel-provider (interop/react-factory pure-react-carousel/CarouselProvider))
(def ui-slider (interop/react-factory pure-react-carousel/Slider))
(def ui-slide (interop/react-factory pure-react-carousel/Slide))
(def ui-button-back (interop/react-factory pure-react-carousel/ButtonBack))
(def ui-button-next (interop/react-factory pure-react-carousel/ButtonNext))

(defsc GovReviewWeek [this {:keys []}]
  {:query [:db/id :gov-review-week/status :gov-review-week/exec-summary :gov-review-week/client-relationship :gov-review/finance :gov-review/scop-schedule :gov-review/submitted-by :gov-review/submitted-at
           ]
   :route-segment ["gov-review-week"]
   :ident [:gov-review/id :db/id]
   :intial-state {:db/id 111111}}
  (dom/p {} "Gov Review Week "))

(def ui-gov-review-week (comp/factory GovReviewWeek))



(defsc ProjectInfo2 [this props]
  {:route-segment ["project-info2"]}

  (dom/p {} "SOMETHING2")
  )
(def ui-project-info2 (comp/factory ProjectInfo2))
(dr/defrouter GovRouter [this props]
  {:router-targets [GovReviewWeek]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed (dom/div "Loading seems to have failed. Try another route.")
    (dom/div "Unknown route")))

(def ui-gov-router (comp/factory GovRouter))



(defsc ProjectInfo [this { :project-panel/keys [id made-up name start-date modified-date last-published-date finish-date project-lead functional-lead technical-lead status phase entity fluxod-name] :as props}]
  {:query [:project-info :project-panel/made-up :project-panel/id :project-panel/start-date :project-panel/modified-date  :some-stuff :project-panel/last-published-date :project-panel/finish-date :project-panel/name :project-panel/status
           :project-panel/phase
           :project-panel/entity
           :project-panel/fluxod-name
           [:resource/options2 '_]
           [df/marker-table '_]
           fs/form-config-join
           {:project-panel/project-lead (comp/get-query users/Resource)} {:project-panel/functional-lead (comp/get-query users/Resource)} {:project-panel/technical-lead (comp/get-query users/Resource)}]
   
   :route-segment   ["project-info" :project-panel/id]
   :ident   (fn [] [:project-info/id id])
   :initial-state {:project-panel/id "0c35708b-32e1-e911-b19b-9cb6d0e1bd60" :project-info "Info2"}
   :will-enter (fn [app {:keys [project-panel/id] :as params}]
                 (js/console.log "params " params)
                 (dr/route-deferred
                  [:project-info/id id]
                  (fn []
                    (df/load! app [:project-info/id id] ProjectInfo)
                    (comp/transact! app [(dr/target-ready {:target [:project-info/id id]})]))))}

  ;(js/console.log "props" props)
  (let [options (get props :resource/options2)]
    (js/console.log "PPPPPPP" props)
    (dom/p {} "P")
    (ui-grid-column {}
     (ui-form {}
              (ui-form-group {}
                             #_(ui-form-field {:width 6}(ui-form-input {:label "Name" :placeholder "Project Name" :readOnly true :value name})))

              (ui-form-group {}
                               (ui-form-field {}
                                              (dom/label {} "Project Status")
                                              (ui-dropdown {:placeholder "Project Status"
                                                            :selection true
                                                            :search true
                                                            :options  
                                                            [{:text "Sales" :value :sales}
                                                             {:text "In Progress" :value :in-progress}
                                                             {:text "Closed" :value :closed}
                                                             {:text "Cancelled" :value :cancelled}]
                                                            :value status
                                                            
                                                            :onChange #(comp/transact! this [(project/set-project-status {:status (keyword (.-value %2))  :project-panel/id id })])}))
                               (ui-form-field {}
                                              (dom/label {} "Project Phase")
                                              (ui-dropdown {:placeholder "Project Phase"
                                                            :selection true
                                                            :search true
                                                            :options  [{:text "Mobilize" :value :mobilize}
                                                                       {:text "Design" :value :design}
                                                                       {:text "Build" :value :build}
                                                                       {:text "Test" :value :test}
                                                                       {:text "Training" :value :training}
                                                                       {:text "HyperCare" :value :hyper-care}]
                                                            :value phase 
                                                            
                                                            :onChange #(comp/transact! this [(project/set-project-phase {:phase (keyword (.-value %2))  :project-panel/id id })])}))


                               (ui-form-field {}
                                              (dom/label {} "Fluxym Entity")
                                              (ui-dropdown {:placeholder "Fluxym Entity"
                                                            :selection true
                                                            :search true
                                                            :options  [{:text "APAC" :value :apac}
                                                                       {:text "FRANCE" :value :france}
                                                                       {:text "NORAM" :value :noram}
                                                                       
                                                                       ]
                                                            :value (or entity :noram) 
                                                            
                                                            :onChange #(comp/transact! this [(project/set-project-entity {:entity (keyword (.-value %2))  :project-panel/id id })])}))
                               )
              (ui-divider {})

              (ui-form-group {}

                             (ui-form-input  {:label "Modified Date" :type "datetime-local" :readOnly true
                                              :value
                                              (apply str (take 16 (str (t/date-time modified-date))))

                                              :onChange
                                              (fn [e d] (js/console.log "data" (.-value (.-target e)) "datein" modified-date ))})
                             
                             (ui-form-input  {:label "Start Date" :type "datetime-local" :readOnly true
                                              :value
                                              (apply str (take 16 (str (t/date-time start-date))))
                                              
                                              })
                             (ui-form-input  {:label "End Date" :type "datetime-local" :readOnly true
                                              :value
                                              (apply str (take 16 (str (t/date-time finish-date))))
                                              
                                              }))
              (ui-divider {})

              
              (ui-form-group {}


                             (ui-form-field {}
                                            (dom/label {} "Project Lead")
                                            (ui-dropdown {:placeholder "Team Lead"
                                                          :selection true
                                                          :search true
                                                          :options  options
                                                          :value (:resource/id project-lead)
                                                          
                                                          :onChange (fn [e d]
                                                                      (js/console.log "id " id)
                                                                      (comp/transact! this [(project/set-project-lead {:lead-id (.-value d) :project-panel/id id })]))}))

                             (ui-form-field {}
                                            (dom/label {} "Functional Lead")
                                            (ui-dropdown {:placeholder "Functional Lead"
                                                          :selection true
                                                          :search true
                                                          :options  options
                                                          :value (:resource/id functional-lead)
                                        ;:value (:resource/id lead)
                                                          
                                                          :onChange #(comp/transact! this [(project/set-functional-lead {:lead-id (.-value %2) :project-panel/id id })])})
                                            
                                            )

                             (ui-form-field {}
                                            (dom/label {} "Technical Lead")
                                            (ui-dropdown {:placeholder "Technical Lead"
                                                          :selection true
                                                          :search true
                                                          :options  options
                                                          :value (:resource/id technical-lead)
                                                          
                                        ;:value (:resource/id lead)
                                                          :onChange #(comp/transact! this [(project/set-technical-lead {:lead-id (.-value %2) :project-panel/id id })])      
                                                          })

                                            
                                            
                                            )

                             

                             )


              (ui-form-group {}
                             (ui-form-field {}
                                            (dom/label {} "Fluxod Name")
                                            (ui-input {:placeholder "Fluxod Name"
                                                       :onChange #()
                                                       :onBlur (fn [e]
                                                                 (comp/transact! this [(project/set-project-fluxod-name {:name (evt/target-value e) :project-panel/id id})]))
                                                       
                                                       :value fluxod-name
                                                       }))

                             

                             )))))









(defsc GovReview [this {:keys [ gov-review/id gov-review/current-weeks gov-review/router] :as props}]
  {:query [:gov-review/id {:gov-review/current-weeks (comp/get-query GovReviewWeek)} {:gov-review/router (comp/get-query GovRouter)}]
   :route-segment ["gov-review" :gov-review/id]
   :ident (fn [] [:component/id :gov-review])
   :initial-state {:gov-review/current-weeks [] :gov-review/router {}}
   :will-enter (fn [app {:keys [gov-review/id] :as params}]
              
              (dr/route-deferred
               [:component/id :gov-review]
               (fn []
                 #_(df/load! app [:project-info/id id] ProjectInfo)
                 (js/console.log "GOV REVIEW")
                 (merge/merge-component! app GovReview {:gov-review/id id})
                 (comp/transact! app [(dr/target-ready {:target [:component/id :gov-review]})]))))}

  (js/console.log "governance review props" props)
  (ui-container {:textAlign :center}
                (ui-button {:basic true} "Week back")
                ;(map ui-gov-review-week current-weeks)
                
                ;; we need to map this from current-weeks prop 
                (ui-step-group {}
                               (ui-step {:onClick #(dr/change-route this (dr/path-to GovReviewWeek)) :active true}
                                        (ui-step-content {}
                                                         (ui-step-title {} "Week 1")
                                                         (ui-step-description {} "Description")))
                               (ui-step {}
                                        (ui-step-content {}
                                                         (ui-step-title {} "Week 2")
                                                         (ui-step-description {} "Description")))
                               (ui-step {}
                                        (ui-step-content {}
                                                         (ui-step-title {} "Week 3")
                                                         (ui-step-description {} "Description"))
                                        )
                               (ui-step {} (ui-step-content {}
                                                            (ui-step-title {} "Week 4")
                                                            (ui-step-description {} "Description"))))
                (ui-button {:basic true} "Week forward")
                ;; router here?
                (ui-gov-router router)
           ))


#_(defsc GovernanceReview2 [this props]
  {:query []
   :route-segment ["governance-review2" :governance-review/id]
   :ident (fn [] [:component/id :governance-review2])}
  (dom/p {} "GOV REVIEW2"))

(dr/defrouter ProjectPanelRouter [this props]
  {:router-targets [ProjectInfo GovReview]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed (dom/div "Loading seems to have failed. Try another route.")
    (dom/div "Unknown route")))

(def ui-project-panel-router (comp/factory ProjectPanelRouter))


(defsc ProjectPanel [this {:keys [project-panel/router id project-panel/current-project] :as props}]
  {:query           [:id {:project-panel/router (comp/get-query ProjectPanelRouter) } {:project-panel/current-project (comp/get-query Project) }]
   :ident   (fn [] [:component/id :project-panel])
   :initial-state {:project-panel/router {}}
   :route-segment   ["project-panel" :id]
   ;:form-fields #{:project-panel/project-lead :project-panel/functional-lead :project-panel/technical-lead}
                                        ;:pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config ProjectPanel data-tree))
   :initLocalState (fn [this props]
                     {:active-item :info
                      :all? false})
   
   :will-enter (fn [app {:keys [id ] :as params}]
                 (js/console.log "params2" params)
                 (dr/route-deferred
                  [:component/id :project-panel]
                  (fn []
                    #_(df/load! app [:project-info/id id] ProjectInfo)
                    (merge/merge-component! app ProjectPanel  {:project-panel/current-project [:project/id (uuid id)] } )
                    (comp/transact! app [(dr/target-ready {:target [:component/id :project-panel]})]))))

    
   #_#_:componentDidMount (fn [this]
                            (js/console.log "SEE?" (comp/props this))
                            )}

  
  (let [active-item (comp/get-state this :active-item)]
    
    
    

                                        ;(dom/h3 {:style {:textAlign "center"}} name)
    


    
    

    [(dom/h3 {} (:project/name current-project))
     (ui-grid-column {:width 4} 
                     (ui-menu {:fluid true :vertical true :tabular true}
                              (ui-menu-item {:name "Info" :active (= active-item :info) :onClick (fn [e]
                                                                                                   (comp/update-state! this assoc :active-item :info )
                                                                                                   (dr/change-route this (dr/path-to ProjectInfo {:project-panel/id (str (:project/id current-project))})))} )
                              (ui-menu-item {:name "Governance Review" :active (= active-item :governance-review) :onClick (fn []
                                                                                                                             
                                                                                                                             (comp/update-state! this assoc :active-item :governance-review)
                                                                                                                             (dr/change-route this (dr/path-to GovReview {:gov-review/id (:project/id current-project)})))} )


                              (ui-menu-item {:name "Risk & Issues" :active (= active-item :risk-issues) :onClick (fn []
                                                                                                                         (comp/update-state! this assoc :active-item :risk-issues )
                                                                                                                             )} )
                              (ui-menu-item {:name "Action List" :active (= active-item :action-list) :onClick (fn []
                                                                                                                       (comp/update-state! this assoc :active-item :action-list )
                                                                                                                       )} )
                              (ui-menu-item {:name "TimeLine" :active (= active-item :timeline) :onClick (fn []
                                                                                                                    (comp/update-state! this assoc :active-item :timeline )
                                                                                                                       )} )))

     (ui-grid-column {:width 12} 
                     (ui-project-panel-router router))]
    

    

    )
   
    )



(defsc RiskIssues [this {:keys [] :as props}]
  {:route-segment ["risk-issues"]}
  (dom/p {} "Risk & Issues"))




(defsc AdminProjects [this {:admin-projects/keys [projects router] :as props}]
  {:query         [{:admin-projects/projects (comp/get-query Project)}
                   {:admin-projects/router (comp/get-query ProjectPanelRouter)}
                   [::uism/asm-id ::session/session]

                   [df/marker-table :admin-projects]]
   
   :ident         (fn [] [:component/id :admin-projects])
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-projects]
                  (fn []
                    
                    (df/load! app :all-projects Project

                              {:target [:component/id :admin-projects :admin-projects/projects]
                               :marker :admin-projects
                               
                               
                               })
                    (comp/transact! app [(dr/target-ready {:target [:component/id :admin-projects]})]))))
   :route-segment ["admin-projects"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   
   
   }

  
  (let [

        current-state (uism/get-active-state this ::session/session)
        logged-in? (= :state/logged-in current-state)
        marker (get props [df/marker-table :admin-projects])]
    (js/console.log "MARKER" props)

    (if logged-in?
      
      (if (df/loading? marker) 

        (ui-loader {:active true :inline :centered} )
        
        (ui-container {:style {:width "60%"}}
                      (dom/h3 {:style {:textAlign "center"}} "Projects" )
                      
                      (ui-table {} (ui-table-header {}
                                                    (ui-table-row {}
                                                                  
                                                                  (ui-table-header-cell {:style {:backgroundColor "#3281b9" :color "#ffffff" :position "sticky" :top 0}} "Project Name")))
                                (ui-table-body {}
                                               (map (fn [p] (ui-table-row {:onClick (fn [e]
                                                                                      #_(js/console.log "XXXXXX"
                                                                                                        (dr/path-to ProjectPanel {:project/id (:project/id p)} ProjectInfo {:project-panel/id (:project/id p)})
)

                                                                                      #_(dr/change-route this 
                                                                                                       (dr/path-to  ProjectInfo2)

                                                                                                       )
                                                                                      #_(dr/change-route this
                                                                                                         (dr/path-to  ProjectInfo))

                                        ;(dr/change-route this (dr/path-to  ProjectInfo {:project-panel/id (:project/id p)}))
                                                                                      
                                                                                      (dr/change-route this ["project-panel" (str (:project/id p)) "project-info"  (str (:project/id p))])
                                                                                      ;(dr/change-route this (dr/path-to ProjectPanel {:id (:project/id p)} ))

                                                                                      

                                                                                      )}
                                                                          
                                                                          (ui-table-cell {} (:project/name p)))) projects)))
                      )
        )
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))







