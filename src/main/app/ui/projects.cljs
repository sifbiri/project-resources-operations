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
   [com.fulcrologic.semantic-ui.elements.label.ui-label :refer [ui-label]]
   [com.fulcrologic.semantic-ui.addons.textarea.ui-text-area :refer [ui-text-area]]
   [com.fulcrologic.semantic-ui.elements.header.ui-header :refer [ui-header]]
   [com.fulcrologic.semantic-ui.elements.header.ui-header-content :refer [ui-header-content]]
   [com.fulcrologic.semantic-ui.elements.header.ui-header-subheader :refer [ui-header-subheader]]
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
   [tick.alpha.api :as t]
   [goog.date]


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

(def month-to-number #(get {t/JANUARY 1 t/FEBRUARY 2 t/MARCH 3 t/APRIL 4 t/MAY 5 t/JUNE 6  t/JULY 7  t/AUGUST 8 t/SEPTEMBER 9  t/OCTOBER 10  t/NOVEMBER 11 t/DECEMBER 12} %))

(defn week-number
  "Week number according to the ISO-8601 standard, weeks starting on
  Monday. The first week of the year is the week that contains that
  year's first Thursday (='First 4-day week'). The highest week number
  in a year is either 52 or 53."
  [ts]
  (when (not (nil? ts))
    (let []

     (let [;year (js/parseInt (str (t/year ts)))
                                        ;month (month-to-number (t/month (t/date-time ts)))
                                        ;date (.getDate ts)
                                        ;day (t/day-of-month (t/date-time ts))

           ]
       (js/console.log  "TS" ts)
       (tt/week-number-of-year (tc/from-date ts))))))







(defn round-to-first-day-of-week [ts]
  (loop [ts ts
         day (t/day-of-week ts)]
    (if (not= day t/MONDAY)
      (recur (t/- ts (t/new-period 1 :days))
             (t/day-of-week (t/- ts (t/new-period 1 :days)))
             
             
             )
       ts)))

(defn get-current-month-weeks []
  (let [current-week (round-to-first-day-of-week (-> (t/today)
                                                     (t/at (t/noon))))
        week-before (t/- current-week (t/new-period 1 :weeks))
        week-before-before (t/- current-week (t/new-period 2 :weeks))
        week-before-before-before (t/- current-week (t/new-period 3 :weeks))]
    (mapv t/inst [ week-before-before-before week-before-before week-before current-week])))

(defn next-weeks [last-week]
  (let [last-week (t/date-time last-week)
        week1 (t/+ last-week (t/new-period 1 :weeks))
        week2 (t/+ last-week (t/new-period 2 :weeks))
        week3 (t/+ last-week (t/new-period 3 :weeks))
        week4 (t/+ last-week (t/new-period 4 :weeks))]
    (mapv t/inst [week1 week2 week3 week4])))

(defn previous-weeks [last-week]
  (let [last-week (t/date-time last-week)
        week1 (t/- last-week (t/new-period 1 :weeks))
        week2 (t/- last-week (t/new-period 2 :weeks))
        week3 (t/- last-week (t/new-period 3 :weeks))
        week4 (t/- last-week (t/new-period 4 :weeks))]
    (mapv t/inst (reverse [week1 week2 week3 week4]))))




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







;; #_(defsc ResourceQ
;;   [this {:resource/keys [id name email-address]}]
;;   {:query [:resource/id :resource/name :resource/email-address]
;;    }
  


;;                                         ;)

  
  
;; 
;(declare ui-project-panel-router)



(def ui-carousel-provider (interop/react-factory pure-react-carousel/CarouselProvider))
(def ui-slider (interop/react-factory pure-react-carousel/Slider))
(def ui-slide (interop/react-factory pure-react-carousel/Slide))
(def ui-button-back (interop/react-factory pure-react-carousel/ButtonBack))
(def ui-button-next (interop/react-factory pure-react-carousel/ButtonNext))



(defsc Comment [this props]
  {:query [:comment/text :comment/color]})

(def Resource (comp/registry-key->class :app.ui.users/Resource))

(defsc GovReviewWeek [this {:gov-review-week/keys [week status exec-summary client-relationship finance scope-schedule submitted-by submitted-at]}]
  {:query [:gov-review-week/week  :gov-review-week/status
           {:gov-review-week/exec-summary (comp/get-query Comment)}
           {:gov-review-week/client-relationship (comp/get-query Comment)}
           {:gov-review-week/finance (comp/get-query Comment)}
           {:gov-review-week/scope-schedule (comp/get-query Comment)}

           {:gov-review-week/submitted-by (comp/get-query Resource)}
           :gov-review-week/submitted-at
           ]
   :route-segment ["gov-review-week" :gov-review-week/week]
   :ident  (fn[][:gov-review-week/week week])
  
  :will-enter (fn [app {:keys [gov-review-week/week] :as params}]
                 (js/console.log "params " params)
                 (dr/route-deferred
                  [:gov-review-week/week week]
                  (fn []
                    (comp/transact! app [(dr/target-ready {:target [:gov-review-week/week week]})]))))
    #_#_:pre-merge (fn [{:keys [current-normalized data-tree]}]
                (merge
                 {:db/id 1} 
                 current-normalized
                 data-tree))
   :intial-state {:db/id 2}}
  (js/console.log "Q" (comp/props this))
  (dom/div {:style {:display "flex" :flex-direction "row" :flex-wrap "wrap"}}
           (dom/label {:style {:alignSelf "flex-start"}} "Status: " (clojure.string/capitalize (name status)))
           (ui-divider {:horizontal true})
           #_(ui-container {}
                         (ui-form {}
                                  #_(ui-table {:textAlign :center}
                                              (ui-table-header {}
                                                               (ui-table-header-cell {} "Header")
                                                               

                                                               ))
                                  
                                  (div {}
                                       (dom/div {:style {:border (str "1px solid" (name (:comment/color exec-summary))) :backgroundColor (:comment/color exec-summary) :paddingTop "8px" :display "flex"}}
                                                (ui-header {:style {:flex 10 } :size :tiny} "Overal Status - Executive Summary")
                                                (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "50px"}}
                                                             (ui-dropdown-menu {}
                                                                               (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange})} )
                                                                               (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green})})
                                                                               (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red})})))
                                                )
                                       (ui-text-area {:value (:comment/text exec-summary)}))


                                  (div {:style {:display "flex"}}

                                       (div {:style {:flex 2}}
                                            (dom/div {:style {:border (str "1px solid" (:comment/color client-relationship)) :backgroundColor (:comment/color client-relationship) :paddingTop "8px" :display "flex"}}
                                                     (ui-header {:style {:flex 10 } :size :tiny} "Client Relationship")
                                                     (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                  (ui-dropdown-menu {}
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange})} )
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green})})
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red})})))
                                                     )
                                            (ui-text-area {}))

                                       (div {:style {:flex 2}}
                                            (dom/div {:style {:border (str "1px solid" (:comment/color finance)) :backgroundColor (:comment/color finance) :paddingTop "8px" :display "flex"}}
                                                     (ui-header {:style {:flex 10 } :size :tiny} "Finance")
                                                     (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                  (ui-dropdown-menu {}
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange})} )
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green})})
                                                                                    (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red})})))
                                                     )
                                            (ui-text-area {}))

                                       (div {:style {:flex 2}}
                                            (dom/div {:style {:border (:comment/color scope-schedule) :backgroundColor (:comment/color scope-schedule) :paddingTop "8px" :display "flex"}}
                                                     (ui-header {:style {:flex 10 } :size :tiny} "Scope & Schedule")
                                                     (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                  (ui-dropdown-menu {}
                                                                                    (ui-dropdown-item {:text "Amber"} )
                                                                                    (ui-dropdown-item {:text "Green"})
                                                                                    (ui-dropdown-item {:text "Red"})))
                                                     )
                                            (ui-text-area {})))
                                  ))
           (ui-divider {:horizontal true})
           (dom/label {:style {:alignSelf "flex-start" :paddingTop "5px"}} "Submitted the " (apply str (take 10 (str submitted-at))) " by " (:resource/email-address submitted-by))
           (ui-button  {:basic true :style {:alignSelf "auto" :marginLeft "459px" :marginTop "5px"}} "Submit")))


(def ui-gov-review-week (comp/factory GovReviewWeek {:keyfn :gov-review-week/week}))


;(declare GovReview)
#_(dr/defrouter GovRouter [this props]
  {:router-targets [ GovReviewWeek]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed (dom/div "Loading seems to have failed. Try another route.")
    (dom/div "Unknown route")))
(declare ProjectPanel)


(defmutation move-current-weeks-next [{:keys [last-week]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:component/id :gov-review :gov-review/current-weeks] [])
                             )
                         (doseq [week (next-weeks (:gov-review-week/week last-week))]
                           (merge/merge-component state GovReviewWeek {:gov-review-week/week week
                                                                       :gov-review-week/status :open})))))
  )






(defsc GovReview [this {:keys [ gov-review/id gov-review/current-weeks gov-review/current-week] :as props}]
  {:query [:gov-review/id {:gov-review/current-weeks (comp/get-query GovReviewWeek)} {:gov-review/current-week (comp/get-query GovReviewWeek)}]
   :route-segment ["gov-review"]
   :ident  (fn [] [:component/id :gov-review])
   :initial-state (fn [p] {:gov-review/current-weeks []})
   :will-enter (fn [app {:keys [gov-review/id] :as params}]
                 (dr/route-deferred
                  
                  [:component/id :gov-review]
                  (fn []
                    #_(df/load! app [:project-info/id id] ProjectInfo)
                    (js/console.log "GOV REVIEW" params)
                                        ;(merge/merge-component! app GovReview {:gov-review/id id})
                    #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (t/instant (round-to-first-day-of-week (-> (t/today)
                                                                                                                      (t/at (t/noon)))))
                                                               :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-week]
                                                               )

                    (comp/transact! app [(project/get-or-create-current-gov-review-week {:gov-review-week/week (t/inst (round-to-first-day-of-week (-> (t/today)
                                                                                                                                               (t/at (t/noon)))))})])
                    #_(df/load! app [:gov-review-week/week (round-to-first-day-of-week (-> (t/today)
                                                                                           (t/at (t/noon))))] GovReviewWeek)
                    

                    (loop [weeks (get-current-month-weeks)
                           index 0]
                      
                      (when (seq weeks)
                        (js/console.log "loop")
                       
                        (comp/transact! app [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index})])
                        
                        (recur (rest weeks) (inc index) )))

                    
                    (comp/transact! app [(dr/target-ready {:target [:component/id :gov-review]})])
                    
                    )               ))}

  (js/console.log "ROUTE123" current-week)
  
  (let [current-tab (some-> (dr/current-route this this) first keyword)]
    (ui-container {:textAlign :center}

                  (ui-icon { :onClick (fn [e]
                                        (let [last-week (:gov-review-week/week (first current-weeks))
                                              previous-weeks (previous-weeks last-week)]


                                          (loop [weeks previous-weeks
                                                 index 0]
                                            
                                            (when (seq weeks)
                                              
                                              
                                              (comp/transact! this [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index})])
                                              
                                              (recur (rest weeks) (inc index) )))

                                                                                    
                                          #_(m/set-value! this :gov-review/current-weeks (mapv (fn [week] [:gov-review-week/week week])
                                                                                             previous-weeks)))
                                        #_(comp/transact! this [(move-current-weeks-next {:last-week (last current-weeks)})])) :name "chevron left"  })
                                        ;(map ui-gov-review-week current-weeks)
                 
                 ;; we need to map this from current-weeks prop 
                  (ui-step-group {}

                                 (map (fn [gov-review-week]

                                        (ui-step {:onClick (fn [e] (comp/transact! this [(project/set-current-gov-week {:gov-review-week gov-review-week})])) :active
                                                  (= (:gov-review-week/week gov-review-week) (:gov-review-week/week current-week) )}
                                                 (ui-step-content {}

                                                                  (ui-step-title {} "Week "(week-number (:gov-review-week/week gov-review-week)))
                                                                  
                                                                  (ui-step-description {} (apply str (take 10 (str (:gov-review-week/week gov-review-week))))))) ) current-weeks))
                 
                 (ui-icon { :name "chevron right" :onClick (fn [e]
                                                             (let [last-week (:gov-review-week/week (last current-weeks))
                                                                   next-weeks (next-weeks last-week)]
                                                               (loop [weeks next-weeks
                                                                      index 0]
                                                                 
                                                                 (when (seq weeks)
                                                                   (js/console.log "loop")
                                                                   
                                                                   (comp/transact! this [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index})])
                                                                   
                                                                   (recur (rest weeks) (inc index) )))

                                                               
                                                               (m/set-value! this :gov-review/current-weeks (mapv (fn [week] [:gov-review-week/week week])
                                                                                                                  next-weeks)))
                                                             #_(comp/transact! this [(move-current-weeks-next {:last-week (last current-weeks)})]))  })
                 ;; router here?
                 (js/console.log "CURRENT WEEK " current-week)
                 (ui-gov-review-week current-week)
                 )))





(defsc ProjectInfo2 [this props]
  {:route-segment ["project-info2"]}

  (dom/p {} "SOMETHING2")
  )
(def ui-project-info2 (comp/factory ProjectInfo2))

(defsc ProjectInfo [this {:project-info/keys [id  name start-date modified-date last-published-date finish-date project-lead functional-lead technical-lead status phase entity fluxod-name] :as props}]
  {:query [:project-info/id :project-info/modified-date :project-info/start-date :project-info/last-published-date :project-info/finish-date :project-info/name :project-info/status
           :project-info/phase
           :project-info/entity
           :project-info/fluxod-name
           [:resource/options2 '_]
           [df/marker-table '_]
           fs/form-config-join
           {:project-info/project-lead (comp/get-query users/Resource)} {:project-info/functional-lead (comp/get-query users/Resource)} {:project-info/technical-lead (comp/get-query users/Resource)}]
   
   :route-segment   ["project-info" :project-info/id]
   :ident   (fn [] [:project-info/id id])
   :initial-state {:project-info/id 1 }
   :will-enter (fn [app {:keys [project-info/id] :as params}]
                 (js/console.log "params " params)
                 (dr/route-deferred
                  [:project-info/id (uuid id)]
                  (fn []
                    (df/load! app [:project-info/id (uuid id)] ProjectInfo)
                    (comp/transact! app [(dr/target-ready {:target [:project-info/id (uuid id)]})]))))}

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
                                                            
                                                            :onChange (fn [e d]
                                                                        (js/console.log "id" id)
                                                                        (comp/transact! this [(project/set-project-status {:status (keyword (.-value d))  :project-info/id id })]))}))
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
                                                            
                                                            :onChange #(comp/transact! this [(project/set-project-phase {:phase (keyword (.-value %2))  :project-info/id id })])}))


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
                                                            
                                                            :onChange #(comp/transact! this [(project/set-project-entity {:entity (keyword (.-value %2))  :project-info/id id })])}))
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
                                                                      (comp/transact! this [(project/set-project-lead {:lead-id (.-value d) :project-info/id id })]))}))

                             (ui-form-field {}
                                            (dom/label {} "Functional Lead")
                                            (ui-dropdown {:placeholder "Functional Lead"
                                                          :selection true
                                                          :search true
                                                          :options  options
                                                          :value (:resource/id functional-lead)
                                        ;:value (:resource/id lead)
                                                          
                                                          :onChange #(comp/transact! this [(project/set-functional-lead {:lead-id (.-value %2) :project-info/id id })])})
                                            
                                            )

                             (ui-form-field {}
                                            (dom/label {} "Technical Lead")
                                            (ui-dropdown {:placeholder "Technical Lead"
                                                          :selection true
                                                          :search true
                                                          :options  options
                                                          :value (:resource/id technical-lead)
                                                          
                                        ;:value (:resource/id lead)
                                                          :onChange #(comp/transact! this [(project/set-technical-lead {:lead-id (.-value %2) :project-info/id id })])      
                                                          })

                                            
                                            
                                            )

                             

                             )


              (ui-form-group {}
                             (ui-form-field {}
                                            (dom/label {} "Fluxod Name")
                                            (ui-input {:placeholder "Fluxod Name"
                                                       :onChange #()
                                                       :onBlur (fn [e]
                                                                 (comp/transact! this [(project/set-project-fluxod-name {:name (evt/target-value e) :project-info/id id})]))
                                                       
                                                       :value fluxod-name
                                                       }))

                             

                             )))))



(declare ProjectPanel)
(declare AdminProjects)



(dr/defrouter ProjectPanelRouter [this props]
  {:router-targets [ProjectInfo GovReview]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed (dom/div "Loading seems to have failed. Try another route.")
    (dom/div "Unknown route")))

(def ui-project-panel-router (comp/factory ProjectPanelRouter))

(defmutation set-current-project-id [{:keys [current-project-id]}]
  (action [{:keys [state app]}]
          (let [ ]
            (js/console.log "TRANSACT" current-project-id)
            (swap! state (fn [state]
                           (-> state
                               (assoc-in [:component/id :project-panel :project-panel/current-project-id] current-project-id))))
            (dr/target-ready! app [:component/id :project-panel])
            ;; (swap! state  merge/merge-component TeamCheckbox
            ;;        {:db/id team-id :team/name name }
            
            ;;        )
            ))

  
  )

(defsc ProjectPanel [this {:keys [project-panel/router project-panel/current-project project-panel/current-project-id] :as props}]
  {:query           [:project-panel/current-project-id { :project-panel/router (comp/get-query ProjectPanelRouter) } {:project-panel/current-project (comp/get-query Project) }
                     [::uism/asm-id '_]]
   :ident   (fn [] [:component/id :project-panel])
   :initial-state {:project-panel/router {}}
   :route-segment   ["project-panel" :project-panel/current-project-id]
   ;:form-fields #{:project-panel/project-lead :project-panel/functional-lead :project-panel/technical-lead}
                                        ;:pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config ProjectPanel data-tree))
   :initLocalState (fn [this props]
                     {:active-item :info
                      :all? false})
   
   :will-enter (fn [app {:keys [project-panel/current-project-id] :as params}]
                 (js/console.log "params2" current-project-id)
                 (dr/route-deferred
                  [:component/id :project-panel]
                  (fn []
                    #_(df/load! app [:project-info/id id] ProjectInfo)
                    #_(merge/merge-component! app ProjectPanel  {:project-panel/current-project [:project/id (uuid current-project-id)] } )
                    (comp/transact! app [(set-current-project-id {:current-project-id current-project-id})])
                    #_(comp/transact! app [

                                         ]))))

    
   #_#_:componentDidMount (fn [this]
                            (js/console.log "SEE?" (comp/props this))
                            )}

  
  (let [active-item (comp/get-state this :active-item)]
    
    
    

                                        ;(dom/h3 {:style {:textAlign "center"}} name)
    


    
    (js/console.log "PROPS" current-project-id)

    [(dom/h3 {} (:project/name current-project))
     (ui-grid-column {:width 4} 
                     (ui-menu {:fluid true :vertical true :tabular true}
                              (ui-menu-item {:name "Info" :active (= active-item :info) :onClick (fn [e]
                                                                                                   (comp/update-state! this assoc :active-item :info )
                                                                                                   (js/console.log "current-project-id" current-project-id)
                                                                                                   (dr/change-route this (dr/path-to ProjectInfo {:project-info/id current-project-id})))} )
                              (ui-menu-item {:name "Governance Review" :active (= active-item :governance-review) :onClick (fn []
                                                                                                                             
                                                                                                                             (comp/update-state! this assoc :active-item :governance-review)

                                                                                                                             
                                                                                                                             ;
                                                                                                                             ;(merge/merge-component! this GovReviewWeek {:db/id 1})
                                                                                                                             (js/console.log "CONSOLE ")
                                                                                                                             ;; check this out! TODO 
                                                                                                                             (dr/change-route this (dr/path-to  GovReview {:gov-review/id current-project-id} )))} )


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






#_(defsc GovernanceReview2 [this props]
  {:query []
   :route-segment ["governance-review2" :governance-review/id]
   :ident (fn [] [:component/id :governance-review2])}
  (dom/p {} "GOV REVIEW2"))








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
                                                                                                        (dr/path-to ProjectPanel {:project/id (:project/id p)} ProjectInfo {:project-info/id (:project/id p)})
)

                                                                                      #_(dr/change-route this 
                                                                                                       (dr/path-to  ProjectInfo2)

                                                                                                       )
                                                                                      #_(dr/change-route this
                                                                                                         (dr/path-to  ProjectInfo))

                                        ;(dr/change-route this (dr/path-to  ProjectInfo {:project-panel/id (:project/id p)}))
                                                                                      
                                                                                      #_(dr/change-route this (dr/path-to  GovReview GovReviewWeek {:db/id 1} ))

                                                                                      (dr/change-route this (dr/path-to ProjectPanel ProjectInfo {:project-info/id (:project/id p) :project-panel/current-project-id (:project/id p)}))
                                                                                      
                                                                                      ;(dr/change-route this (dr/path-to ProjectPanel {:id (:project/id p)} ))

                                                                                      

                                                                                      )}
                                                                          
                                                                          (ui-table-cell {} (:project/name p)))) projects)))
                      )
        )
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))













