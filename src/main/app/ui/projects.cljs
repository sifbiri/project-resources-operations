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
  {:query [:db/id :comment/text :comment/color fs/form-config-join]
   :form-fields #{:comment/text :comment/color}
   })

(def Resource (comp/registry-key->class :app.ui.users/Resource))

(defsc GovReviewWeek [this {:gov-review-week/keys [week status exec-summary-text exec-summary-color client-relationship-text client-relationship-color  finance-text finance-color scope-schedule-text scope-schedule-color  submitted-by submitted-at project]}]
  {:query [:gov-review-week/week  :gov-review-week/status
           fs/form-config-join
           :gov-review-week/exec-summary-text
           :gov-review-week/exec-summary-color

           
           {:gov-review-week/project (comp/get-query Project)}

           
           :gov-review-week/client-relationship-text
           :gov-review-week/client-relationship-color
           
           :gov-review-week/finance-text
           :gov-review-week/finance-color
           
           :gov-review-week/scope-schedule-text
           :gov-review-week/scope-schedule-color

           {:gov-review-week/submitted-by (comp/get-query Resource)}
           :gov-review-week/submitted-at
           ]
   :form-fields #{:gov-review-week/status :gov-review-week/finance}
   :route-segment ["gov-review-week" :gov-review-week/week]
   :ident  (fn[][:gov-review-week/week week])
  
  :will-enter (fn [app {:keys [gov-review-week/week] :as params}]
                 (js/console.log "params " params)
                 (dr/route-deferred
                  [:gov-review-week/week week]
                  (fn []
                    #_(merge/merge-component! app GovReviewWeek {:gov-review-week/week week})
                    
                    (comp/transact! app [(dr/target-ready {:target [:gov-review-week/week week]})]))))
   ;:pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config GovReviewWeek data-tree))
   :intial-state {}}
  (js/console.log "QQQQ" (comp/props this))
  (let [finance-color-handler
        (fn [e d]
          (let [new-color (keyword (.-key (.-text d)))]
            (m/set-value! this :gov-review-week/finance-color new-color)))

        scope-schedule-color-handler (fn [e d]
                                       (let [new-color (keyword (.-key (.-text d)))]
                                         (m/set-value! this :gov-review-week/scope-schedule-color new-color)))


        exec-summary-color-handler (fn [e d]
                                     (let [new-color (keyword (.-key (.-text d)))]
                                       (m/set-value! this :gov-review-week/exec-summary-color new-color)))

        client-relationship-color-handler (fn [e d]
                                            (let [new-color (keyword (.-key (.-text d)))]
                                              (m/set-value! this :gov-review-week/client-relationship-color new-color)))
        get-color #(if (= % :orange) "DarkOrange" %)]
    
    (dom/div {:style {:display "flex" :flexDirection "row" :flexWrap "wrap"}}
            (dom/label {:style {:alignSelf "flex-start"}} "Status: " (clojure.string/capitalize (name status)))
            (ui-divider {:horizontal true})
            (ui-container {}
                          (ui-form {}
                                   #_(ui-table {:textAlign :center}
                                               (ui-table-header {}
                                                                (ui-table-header-cell {} "Header")
                                                                

                                                                ))
                                   ;; EXEC SUMMARY
                                   
                                   (div {}
                                        (dom/div {:style {:border (str "1px solid " (name (get-color exec-summary-color))) :backgroundColor (get-color exec-summary-color) :paddingTop "8px" :display "flex"}}
                                                 (ui-header {:style {:flex 10 :color :white} :size :tiny} "Overal Status - Executive Summary")
                                                 (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "50px"}}
                                                              (ui-dropdown-menu {}
                                                                                (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange}) :onClick exec-summary-color-handler} )
                                                                                (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green}) :onClick exec-summary-color-handler})
                                                                                (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red}) :onClick exec-summary-color-handler})))
                                                 )
                                        (ui-text-area {:rows 8 :value exec-summary-text
                                                       :onChange (fn [e]
                                                                   
                                                                   (m/set-value! this :gov-review-week/exec-summary-text (evt/target-value e)))}))

                                   ;; CLIENT RELATIONSHIP
                                   
                                   (div {:style {:display "flex"}}

                                        (div {:style {:flex 2}}
                                             (dom/div {:style {:border (str "1px solid " (name (get-color client-relationship-color))) :backgroundColor (get-color client-relationship-color) :paddingTop "8px" :display "flex"}}
                                                      (ui-header {:style {:flex 10 :color "white"} :size :tiny } "Client Relationship")
                                                      (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                   (ui-dropdown-menu {}
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange}) :onClick client-relationship-color-handler} )
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green}) :onClick client-relationship-color-handler})
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red}) :onClick client-relationship-color-handler})))
                                                      )
                                             (ui-text-area {:rows 5
                                                            :value client-relationship-text
                                                            :onChange (fn [e]
                                                                        (m/set-value! this :gov-review-week/client-relationship-text (evt/target-value e)))}))


                                        ;; FINANCE 
                                        (div {:style {:flex 2}}
                                             (dom/div {:style {:border (str "1px solid "  (name (get-color finance-color))) :backgroundColor (get-color finance-color)  :paddingTop "8px" :display "flex"}}
                                                      (ui-header {:style {:flex 10  :color "white"} :size :tiny} "Finance")
                                                      (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                   (ui-dropdown-menu {}
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange})
                                                                                                        :onClick finance-color-handler
                                                                                                        })
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green})
                                                                                                        :onClick finance-color-handler})
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red
                                                                                                                         })
                                                                                                        :onClick finance-color-handler})))
                                                      )
                                             (ui-text-area {:rows 5
                                                            :value finance-text
                                                            :onChange (fn [e]
                                                                        (m/set-value! this :gov-review-week/finance-text (evt/target-value e)))}))
                                        ;; SCOPE & SCHEDULE

                                        (div {:style {:flex 2}}
                                             (dom/div {:style {:border (str "1px solid " (name (get-color scope-schedule-color))) :backgroundColor (get-color scope-schedule-color)  :paddingTop "8px" :display "flex"}}
                                                      (ui-header {:style {:flex 10 :color "white"} :size :tiny} "Scope & Schedule")
                                                      (ui-dropdown {:icon (ui-icon {:name "angle down" })  :style {:flex 1 :position "relative" :left "2px"}}
                                                                   (ui-dropdown-menu {}
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :orange :empty true :key :orange}) :onClick scope-schedule-color-handler} )
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :green :empty true :key :green}) :onClick scope-schedule-color-handler})
                                                                                     (ui-dropdown-item {:text (ui-label {:circular true :color :red :empty true :key :red}) :onClick scope-schedule-color-handler})))
                                                      )
                                             (ui-text-area {:rows 5
                                                            :value scope-schedule-text
                                                            :onChange (fn [e]
                                                                        (m/set-value! this :gov-review-week/scope-schedule-text (evt/target-value e)))})))
                                   ))
            (ui-divider {:horizontal true})
            (if (= status :submitted)
              (dom/label {:style {:alignSelf "flex-start" :paddingTop "5px"}} "Submitted the " (apply str (take 21 (str submitted-at)))
                         (when (or (not (keyword? (:resource/email-address submitted-by)))
                                   (nil? (:resource/email-address submitted-by)))
                           (str " by " (:resource/email-address submitted-by))))
              (dom/label {:style {:alignSelf "flex-start" :paddingTop "5px"}} "-"))
            )))


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
   :route-segment ["gov-review" :gov-review/id]
   :ident  (fn [] [:component/id :gov-review])
   :initial-state (fn [p] {:gov-review/current-weeks [[:gov-review-week/week (t/instant (t/now))][:gov-review-week/week (t/instant (t/now))] [:gov-review-week/week (t/instant (t/now))] [:gov-review-week/week (t/instant (t/now))]]})
   :will-enter (fn [app {:keys [gov-review/id] :as params}]
                 (dr/route-deferred
                  
                  [:component/id :gov-review]
                  (fn []
                    #_(df/load! app [:project-info/id id] ProjectInfo)
                    (js/console.log "GOV REVIEW" params)

                    
                    (merge/merge-component! app GovReview {:gov-review/id (uuid id)})
                    #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (t/instant (round-to-first-day-of-week (-> (t/today)
                                                                                                                                  (t/at (t/noon)))))
                                                               :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-week]
                                                               )

                    
                    (comp/transact! app [(project/get-or-create-current-gov-review-week {:gov-review-week/week (t/inst (round-to-first-day-of-week (-> (t/today)
                                                                                                                                                      (t/at (t/noon)))))
                                                                                         :project/id (uuid (:gov-review/id params))})])
                    #_(df/load! app [:gov-review-week/week (round-to-first-day-of-week (-> (t/today)
                                                                                           (t/at (t/noon))))] GovReviewWeek)
                    

                    (loop [weeks (get-current-month-weeks)
                           index 0]
                      
                      (when (seq weeks)
                        (js/console.log "loop")
                        #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (first weeks)
                                                                     :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-weeks index]
                                                                     )
                        (comp/transact! app [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index :project/id (uuid (:gov-review/id params))})])
                        
                        (recur (rest weeks) (inc index) )))

                    
                    (comp/transact! app [(dr/target-ready {:target [:component/id :gov-review]})])
                    
                    )               ))}

  (js/console.log "ROUTE123" id)
  
  (let [current-tab (some-> (dr/current-route this this) first keyword)]
    (ui-container {:textAlign :center :style {:fontSize "85%"}}

                  (dom/div {:style {:display "flex" :alignItems "center" :alignContent "center" :flexWrap "nowrap"  }}
                           (ui-icon { :onClick (fn [e]
                                                 (let [last-week (:gov-review-week/week (first current-weeks))
                                                       previous-weeks (previous-weeks last-week)]


                                                   (loop [weeks previous-weeks
                                                          index 0]
                                                     
                                                     (when (seq weeks)
                                                       (js/console.log "loop")
                                                       #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (first weeks)
                                                                                                    :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-weeks index]
                                                                                                    )
                                                       (comp/transact! this [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index :project/id id})])
                                                       
                                                       (recur (rest weeks) (inc index) )))

                                                   
                                                   #_(doseq [
                                                             ]
                                                       (merge/merge-component! SPA GovReviewWeek {:gov-review-week/week week
                                                                                                  :gov-review-week/status :open}))


                                                   #_(m/set-value! this :gov-review/current-weeks (mapv (fn [week] [:gov-review-week/week week])
                                                                                                        previous-weeks)))
                                                 #_(comp/transact! this [(move-current-weeks-next {:last-week (last current-weeks)})])) :name "chevron left"  })
                                        ;(map ui-gov-review-week current-weeks)
                           
                           ;; we need to map this from current-weeks prop 
                           (ui-step-group {:style {:flex 1} :fluid true :size :mini}
                                          
                                          (mapv (fn [gov-review-week]

                                                  (ui-step {:onClick
                                                            (fn [e] (m/set-value! this :gov-review/current-week [:gov-review-week/week (:gov-review-week/week gov-review-week)]))

                                                            ;(fn [e] (comp/transact! this [(project/set-current-gov-week {:gov-review-week gov-review-week})]))
                                                            :active
                                                            (= (:gov-review-week/week gov-review-week) (:gov-review-week/week current-week) )

                                                            :style {:color (if (= (:gov-review-week/status gov-review-week) :open) "Gray" :black)}
                                        ;:completed (= (:gov-review-week/status gov-review-week) :submitted)
                                                            
                                                            }
                                                           
                                                           (ui-icon (cond (= (:gov-review-week/status gov-review-week) :submitted) {:name  "check circle outline" :color :green}
                                                                          (= (:gov-review-week/status gov-review-week) :overdue) {:name "times circle outline" :color :red}))

                                                           (ui-step-content {}
                                                                            (ui-step-title {} "Week "(week-number (:gov-review-week/week gov-review-week)))
                                                                            
                                                                            (ui-step-description {:style {:color (if (= (:gov-review-week/status gov-review-week) :open) "Gray" :black)}}
                                                                                                 (apply str (take 15 (str (:gov-review-week/week gov-review-week))))))) ) current-weeks)
                                          )
                           
                           (ui-icon { :style {:position "relative" :left "3px"} :name "chevron right" :onClick (fn [e]
                                                                                                                (let [last-week (:gov-review-week/week (last current-weeks))
                                                                                                                      next-weeks (next-weeks last-week)]


                                                                                                                  (loop [weeks next-weeks
                                                                                                                         index 0]
                                                                                                                    
                                                                                                                    (when (seq weeks)
                                                                                                                      (js/console.log "loop")
                                                                                                                      #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (first weeks)
                                                                                                                                                                   :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-weeks index]
                                                                                                                                                                   )
                                                                                                                      (comp/transact! this [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index :project/id id})])
                                                                                                                      
                                                                                                                      (recur (rest weeks) (inc index) )))

                                                                                                                  
                                                                                                                  #_(doseq [
                                                                                                                            ]
                                                                                                                      (merge/merge-component! SPA GovReviewWeek {:gov-review-week/week week
                                                                                                                                                                 :gov-review-week/status :open}))


                                                                                                                  #_(m/set-value! this :gov-review/current-weeks (mapv (fn [week] [:gov-review-week/week week])
                                                                                                                                                                       previous-weeks)))
                                                                                                                #_(comp/transact! this [(move-current-weeks-next {:last-week (last current-weeks)})]))  }))


                  ;; router here?
                 (js/console.log "CURRENT WEEK " current-week)
                 (ui-gov-review-week current-week)
                 (ui-button  {:basic true :style {:alignSelf "flex-end" :position "relative" :left "380px" :marginTop "10px"} :onClick (fn [e] (comp/transact! this [(project/submit-current-gov-review-week {:gov-review-week current-week :project/id id})]))} "Submit")
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
                    #_(merge/merge-component! app ProjectPanel  {:project-panel/current-project {:project/id (uuid current-project-id)} } )

                    (df/load! app [:project/id (uuid current-project-id)] Project {:target [:component/id :project-panel :project-panel/current-project]})
                    (comp/transact! app [(set-current-project-id {:current-project-id current-project-id})])
                    #_(comp/transact! app [

                                         ]))))

    
   #_#_:componentDidMount (fn [this]
                            (js/console.log "SEE?" (comp/props this))
                            )}

  
  (let [active-item (comp/get-state this :active-item)]
    
    
    

                                        ;(dom/h3 {:style {:textAlign "center"}} name)
    


    
    (js/console.log "PROPS" (comp/props this))

    [(dom/h3 {:style {:color "#3281b9"}} (:project/name current-project))
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
                                                                                                                             (dr/change-route this (dr/path-to  GovReview {:gov-review/id current-project-id  } )))} )


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

(defsc AdminProject [this {:keys [project/id]}]
  {:query [:project/id :project/name {:project-info/project-lead [:resource/id :resource/name]} 
           :gov-review-week/exec-summary-color
           :gov-review-week/client-relationship-color
           :gov-review-week/finance-color
           :gov-review-week/scope-schedule-color
           :project-info/status]
   :ident (fn [] [:admin-project/id id])})


(defsc AdminProjects [this {:admin-projects/keys [admin-projects router] :ui/keys [in-progress? cancelled? closed? sales?] :as props}]
  {:query         [{:admin-projects/admin-projects  (comp/get-query AdminProject)}
                   {:admin-projects/router (comp/get-query ProjectPanelRouter)}
                   [::uism/asm-id ::session/session]

                   :ui/in-progress?
                   :ui/closed?
                   :ui/sales?
                   :ui/cancelled?

                   [df/marker-table :admin-projects]]
   
   :ident         (fn [] [:component/id :admin-projects])
   :initial-state (fn [p] {:ui/in-progress? true :ui/cancelled? false :ui/closed? false :ui/sales? false})
   :initLocalState (fn [this props]
                     {:active-index -1})
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-projects]
                  (fn []
                    
                    (df/load! app :all-admin-projects AdminProject
                              {:target [:component/id :admin-projects :admin-projects/admin-projects]
                               :marker :admin-projects
                               })
                    (comp/transact! app [(dr/target-ready {:target [:component/id :admin-projects]})]))))
   :route-segment ["admin-projects"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   
   
   }

  
  (let [

        current-state (uism/get-active-state this ::session/session)
        active-index (comp/get-state this :active-index)
        handleClick (fn [e v]
                      (let [active-index (comp/get-state this :active-index)
                            index (.-index  v)
                            new-index (if (= active-index index) -1 index)]
                        (comp/update-state! this assoc  :active-index new-index)
                        ))
        logged-in? (= :state/logged-in current-state)
        marker (get props [df/marker-table :admin-projects])]
    (js/console.log "MARKER" props)

    (if logged-in?
      
      (if (df/loading? marker) 

        (ui-loader {:active true :inline :centered} )
        
        
        [(ui-grid-column  {:width 3 :style {:color "#3281b9"}}
                          (ui-accordion
                             {:as Menu  :vertical true :style {:color "#3281b9"}}
                             
                             (ui-menu-item {} (ui-accordion-title
                                               {:active (= active-index 1)
                                                :content "Status"
                                                :index 1
                                                :onClick handleClick
                                                :style {:color "#3281b9"}})
                                           (ui-accordion-content {:active (= active-index 1)
                                                                  :content (ui-form {}
                                                                                    (ui-form-group {:grouped true}
                                                                                                   (ui-form-field {}                                                                                                                   
                                                                                                                  (dom/div :.ui.checkbox
                                                                                                                           (dom/input   {
                                                                                                                                         :type "Checkbox"
                                                                                                                                         :checked  in-progress?
                                        
                                                                                                                                         :style {:padding "10px"}
                                                                                                                                         
                                                                                                                                         :onChange (fn [_ d]
                                                                                                                                                     (m/toggle! this :ui/in-progress?))})
                                                                                                                           (dom/label {:style {:color "#3281b9" }} "In Progress")))
                                                                                                   (ui-form-field {}                                                                                                                   
                                                                                                                  (dom/div :.ui.checkbox
                                                                                                                           (dom/input   {
                                                                                                                                         :type "Checkbox"
                                                                                                                                         :checked sales?
                                        
                                                                                                                                         :style {:padding "10px"}
                                                                                                                                         
                                                                                                                                         :onChange (fn [_ d]
                                                                                                                                                     (m/toggle! this :ui/sales?))})
                                                                                                                           (dom/label {:style {:color "#3281b9" }} "Sales")))
                                                                                                   (ui-form-field {}                                                                                                                   
                                                                                                                  (dom/div :.ui.checkbox
                                                                                                                           (dom/input   {
                                                                                                                                         :type "Checkbox"
                                                                                                                                         :checked closed?
                                        
                                                                                                                                         :style {:padding "10px"}
                                                                                                                                         
                                                                                                                                         :onChange (fn [_ d]
                                                                                                                                                     (m/toggle! this :ui/closed?))})
                                                                                                                           (dom/label {:style {:color "#3281b9" }} "Closed")))
                                                                                                   (ui-form-field {}                                                                                                                   
                                                                                                                  (dom/div :.ui.checkbox
                                                                                                                           (dom/input   {
                                                                                                                                         :type "Checkbox"
                                                                                                                                         :checked cancelled?
                                        
                                                                                                                                         :style {:padding "10px"}
                                                                                                                                         
                                                                                                                                         :onChange (fn [_ d]
                                                                                                                                                     (m/toggle! this :ui/cancelled?))})
                                                                                                                           (dom/label {:style {:color "#3281b9" }} "Cancelled")))
                                                                                                   ))}))
                             ))
         (ui-grid-column {:width 13}
                         #_(dom/h3 {:style {:textAlign "center"}} "Projects" )
                         (ui-table {:color :blue :style {:fontSize "85%"} :singleLine true :striped true :celled true}
                                   (ui-table-header {}
                                                    (ui-table-row {}
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Project Name")
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Project Lead")
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Overall")
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Client Relationship")
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Finance")
                                                                  (ui-table-header-cell {:style {:position "sticky" :top 0} } "Scope & Schedule")))
                                   (ui-table-body {}
                                                  (mapv (fn [p] (ui-table-row {:onClick (fn [e]
                                                                                          

                                                                                          (dr/change-route this (dr/path-to ProjectPanel ProjectInfo {:project-info/id (:project/id p) :project-panel/current-project-id (:project/id p)}))
                                                                                          
                                        ;(dr/change-route this (dr/path-to ProjectPanel {:id (:project/id p)} ))

                                                                                          

                                                                                          )}
                                                                              
                                                                              (ui-table-cell {} (str (:project/name p)))
                                                                              (ui-table-cell {} (let [project-lead (get-in p [:project-info/project-lead :resource/name])]
                                                                                                  (if (keyword? project-lead)
                                                                                                    ""
                                                                                                    project-lead)))
                                                                              

                                                                              (ui-table-cell {:textAlign :center} (ui-label {:circular true :color (:gov-review-week/exec-summary-color p) :empty true :key (:gov-review-week/exec-summary-color p)}))
                                                                              (ui-table-cell {:textAlign :center} (ui-label {:circular true :color (:gov-review-week/client-relationship-color p) :empty true :key (:gov-review-week/exec-summary-color p)}))
                                                                              (ui-table-cell {:textAlign :center} (ui-label {:circular true :color (:gov-review-week/finance-color p) :empty true :key (:gov-review-week/finance-color p)}))
                                                                              (ui-table-cell {:textAlign :center} (ui-label {:circular true :color (:gov-review-week/scope-schedule-color p) :empty true :key (:gov-review-week/scope-schedule-color p)}))

                                                                              
                                                                              )) (filter (if (or in-progress? sales? closed? cancelled?)
                                                                                           (fn [p] (some identity ((juxt (cond-> p in-progress?
                                                                                                                                 #(= (:project-info/status %) :in-progress)
                                                                                                                                 sales?
                                                                                                                                 #(= (:project-info/status %) :sales)
                                                                                                                                 closed?
                                                                                                                                 #(= (:project-info/status %) :closed)
                                                                                                                                 cancelled?
                                                                                                                                 #(= (:project-info/status %) :cancelled))) p)))
                                                                                           identity) admin-projects)))))]
        )
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))













