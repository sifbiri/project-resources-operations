(ns app.ui.workplans
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]
   [app.ui.users :as users]
   [app.model.workplan :as workplan]
   [app.model.import :as import]
   [com.fluxym.model.account :as account]

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
   ["react-flag-kit" :as FlagIcon]
   ["react-sticky-table" :as StickyTable]
   [app.model.resource :as resource]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [app.model.work-line :as work-line]
   ["semantic-ui-react/dist/commonjs/modules/Dropdown/Dropdown" :default Dropdown]
   [clojure.set :as set]
   [clojure.string :as str]
   [app.math :as math2]
   ["react-number-format" :as NumberFormat]
   ["react-country-flag" :as  ReactCountryFlag]
                                        ;["react-collapsing-table" :as ReactCollapsingTable]

                                        ;["react-calendar-timeline" :as TimeLine]
   ["semantic-ui-calendar-react" :as SemanticUICalendar]
   ["react-timeline-9000" :as ReactTimeLine]
   ["react-google-charts" :as ReactGoogleCharts]
   [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.dom.events :as evt]
   [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
   [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
   [com.fulcrologic.fulcro.algorithms.normalize :as normalize]
   [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
   [com.fulcrologic.semantic-ui.elements.flag.ui-flag :refer [ui-flag]]
   [com.fulcrologic.semantic-ui.addons.radio.ui-radio :refer [ui-radio]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-footer :refer [ui-table-footer]]
   [com.fulcrologic.semantic-ui.modules.popup.ui-popup :refer [ui-popup]]
   [com.fulcrologic.semantic-ui.modules.popup.ui-popup-content :refer [ui-popup-content]]
   [com.fulcrologic.semantic-ui.modules.popup.ui-popup-header :refer [ui-popup-header]]
   [app.model.item :as item2]
   [com.fulcrologic.rad.type-support.decimal :as math]
   [com.fluxym.model.line-item :as line-item]
   [com.fluxym.model.item :as item]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   ["semantic-ui-react/dist/commonjs/modules/Dropdown/Dropdown" :default Dropdown]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]

   [com.fulcrologic.semantic-ui.collections.form.ui-form-checkbox :refer [ui-form-checkbox]]



   ;; semantic comoponents


   [com.fulcrologic.semantic-ui.addons.select.ui-select :refer [ui-select]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal :refer [ui-modal]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-content :refer [ui-modal-content]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-description :refer [ui-modal-description]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-actions :refer [ui-modal-actions]]
   [com.fulcrologic.semantic-ui.modules.modal.ui-modal-header :refer [ui-modal-header]]
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
   [com.fulcrologic.semantic-ui.modules.search.ui-search :refer [ui-search]]

   [com.fulcrologic.semantic-ui.modules.search.ui-search-category :refer [ui-search-category]]
   [com.fulcrologic.semantic-ui.modules.search.ui-search-result :refer [ui-search-result]]
   [com.fulcrologic.semantic-ui.modules.search.ui-search-results :refer [ui-search-results]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-content :refer [ui-step-content]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-description :refer [ui-step-description]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-group :refer [ui-step-group]]
   [com.fulcrologic.semantic-ui.elements.step.ui-step-title :refer [ui-step-title]]

   [com.fulcrologic.rad.form :as form :refer [defsc-form]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]

   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [cljs-time.core :as tt]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as tc]
   [taoensso.timbre :as log :refer-macros [log	trace  debug  info  warn  error	 fatal	report
                                           logf tracef debugf infof warnf errorf fatalf reportf
                                           spy get-env]]
   [tick.alpha.api :as t]
   [goog.date]


                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]


   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]

   ["semantic-ui-react/dist/commonjs/elements/Button/Button" :default Button]

   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]

   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]
   ))


(defsc TimeSheet [this {:keys []}]
  {:query [:timesheet/start-fluxod
           :timesheet/end-fluxod
           :timesheet/start-ms
           :timesheet/end-ms
           :timesheet/work-fluxod
           :timesheet/work-ms]})




(defsc ResourceTimeSheet [this {:resource-ts/keys [name timesheets ] :keys [workplan/max-date workplan/min-date] :as props}]
  {:query [:resource-ts/id :resource-ts/name :workplan/max-date
           :workplan/min-date
           [:ui/workplan-count '_]
           {:resource-ts/timesheets (comp/get-query TimeSheet)} ]
   :ident :resource-ts/id
   }
  (let [from (some-> timesheets last :timesheet/end-ms)
        row-so-far (count timesheets)
        row-count (get props :ui/workplan-count)
        to-pad (- row-count row-so-far)]
    
    (when
        (seq timesheets)
      (ui-table-row
       {}
       [(ui-table-cell
         {:style
          {:position "sticky"
           :left  0
           :background "white"
           :color "black"
           
           }} name)
        
        (mapv
         (fn [timesheet]
           (cond
             (and (:timesheet/work-ms timesheet)
                  (:timesheet/work-fluxod timesheet))

             (ui-popup {:basic true
                        :trigger (ui-table-cell {:style {:background "lightBlue"}}
                                                (str (goog.string.format "%.1f" (:timesheet/work-fluxod timesheet))
                                                     " | "
                                                     (goog.string.format "%.1f" (:timesheet/work-ms timesheet)))
                                                )}
                       (ui-popup-content
                        {:style {:fontSize "80%"}}

                        (div {} "Actuals End: " (apply str (take 10 (str (:timesheet/end-fluxod timesheet))))
                             
                             )
                        (div {} "Forecast Start: " (apply str (take 10 (str (:timesheet/start-ms timesheet)))))))
             
             
             (:timesheet/work-fluxod timesheet)
             (ui-table-cell {:style {:background "lightGray"}}
                            (goog.string.format "%.1f"
                                                (:timesheet/work-fluxod timesheet)))
             :else
             (ui-table-cell {} (goog.string.format "%.1f" (:timesheet/work-ms timesheet)))
             ))
         #_(fn [timesheet]
             (ui-table-cell {} (str (:timesheet/work-fluxod timesheet) " X " (:timesheet/work-ms timesheet))))
         timesheets)

        (mapv
           #(ui-table-cell {} "")
           (range 0    to-pad))
        ]))))

(defsc ResourceTimeSheet2 [this {:keys []}]
  {:query [:resource-ts/id :resource-ts/name :resource-ts/a]})


(def ui-resource-timesheet (comp/factory ResourceTimeSheet {:keyfn :resource-ts/id}))
(defn format-date
  [date & {:keys [by-week] :or {by-week false}}]
  (if by-week
    (let [week-number (workplan/week-number date)
          year (apply str (drop 2 (str (t/year date))))]
      (str "Week " week-number " " ))
    
    (str (str/capitalize (str (t/month date))) " "
         (apply str (drop 2 (str (t/year date)))))))



(defsc WorkPlan2 [this {:workplan/keys [ max-date min-date resources-ts] :resource-ts/keys [start-date end-date] :as props :ui/keys [by-month? by-week?]}]
  {:query [:workplan/id {:workplan/resources-ts (comp/get-query ResourceTimeSheet)}
           :ui/by-month?
           :ui/by-week?
           :resource-ts/start-date :resource-ts/end-date
           :workplan/max-date :workplan/min-date
           [df/marker-table '_]]
   :ident :workplan/id
   :initial-state {:ui/by-month? true :ui/by-week? false}
   :pre-merge (fn [{:keys [current-normalized data-tree] :as params}]
                
                (merge {:ui/by-month? false
                        :ui/by-week? true}
                       current-normalized
                       data-tree))
   :will-enter
   (fn [app {:keys [workplan/id]}]
     (dr/route-deferred
      [:workplan/id (uuid id)]
      (fn []
        (df/load! app [:workplan/id (uuid id)] WorkPlan2
                  {:params {:by :week} :marker :workplan
                   :post-mutation `workplan/set-workplan-count-init
                   :post-mutation-params {:ident [:workplan/id (uuid id)]}
                   })

        ;(comp/transact! this [(workplan/set-workplan-count {:count (count (workplan/dates-from-to min-date max-date {:dates (if by-week? :months :weeks)})) } )])
        (comp/transact! app [(dr/target-ready {:target  [:workplan/id (uuid id)]})]))))
   
   :route-segment ["workplan2" :workplan/id]
   }

  
  (let [status (get-in props [df/marker-table :workplan] )
        
        #_#_supposed (count (workplan/dates-from-to min-date max-date {:dates (if by-week? :weeks :months)}))
        #_#_added (- supposed total)]
    (ui-container
     {}
     (if (df/loading? status)
       (ui-loader {:active true})
       [(dom/div {:style {:display "flex" :flexDirection "row" :fontSize "80%"}}
         
                 (dom/div {:style {:border "1px solid black"
                                   :width "18px"
                                   :height "18px"
                                   :background "lightGray"
                                   :marginLeft "20px"
                                   :marginRight "33px"
                                   }}
                          (dom/label {:style {:marginLeft "25px"}} "Actuals"))
                 (dom/div {:style {:border "1px solid black"
                                   :width "18px"
                                   :height "18px"
                                   :marginLeft "25px"}}
                          (dom/label {:style {:marginLeft "25px"}} "Forecast"))
                 (dom/div {:style {:border "1px solid black"
                                   :width "18px"
                                   :height "18px"
                                   :background "lightBlue"
                                   :marginLeft "75px"}}
                          (dom/label {:style {:marginLeft "25px"}} "Both"))


                 (ui-form
                  {:style {:marginLeft "400px"}}
                  (ui-form-group
                   {}
                   (ui-form-field
                    {:inline true}
                    (dom/label {} "By Month")
                    (ui-radio  { :checked by-month?
                                :onClick
                                (fn [_]
                                  (m/toggle! this :ui/by-month?)
                                  (m/toggle! this :ui/by-week?)
                                  (comp/transact! this [(workplan/set-workplan-count {:count (count (workplan/dates-from-to min-date max-date {:dates (if by-week? :months :weeks)})) } )])
                                  (df/refresh! this {:params {:by (if by-month? :week :month)}}))}))
                   (ui-form-field
                    {:inline true}
                    (dom/label {} "By Week")
                    (ui-radio  { :checked by-week?
                                :onClick
                                (fn [_]
                                  (m/toggle! this :ui/by-month?)
                                  (m/toggle! this :ui/by-week?)
                                  (comp/transact! this [(workplan/set-workplan-count {:count (count (workplan/dates-from-to min-date max-date {:dates (if by-week? :months :weeks)})) } )])
                                  (df/refresh! this {:params {:by  (if by-week? :month :week)}}))}))
                   ))
                 
                 
                 )
        
        (dom/div
         {:style
          {:overflow "scroll"
           :max-width "880px"}}
         (ui-table
          {:color :blue
           :celled true
          ; :compact true
           ;:fixed true
           :textAlign :center
           :singleLine true
                                        ;:striped true
           :style {:fontSize "85%"
                                        ;:width "850px"
                                        ;:max-height "900px"
                   ;:overflow "scroll"
                                        ;:display "block"
                                        ;:height "600px"
                   #_#_:overflowX "scroll"}}
          (ui-table-header
           {:style
            {:top 0
             :position "sticky"}}
           (ui-table-row
            {}
            (ui-table-header-cell {:style
                                   {:position "sticky"
                                    :left  0
                                    :top 0
                                    :background "white"
                                    :color "black"
                                    :zIndex 1
                                    }}  "Resource")

            

            (mapv #(ui-popup {:basic true
                              :trigger (ui-table-header-cell
                                        {:singleLine true
                                         :style {:color "black"
                                                 :background "white"
                                                 ;:width "200px"
                                                 :position "sticky"
                                                 :top 0}}
                                        (format-date % :by-week by-week?))}
                             (ui-popup-content
                              {:style {:fontSize "80%"}}
                              (str (str/capitalize (str (t/month %))) " " (t/year %))
                              ))
                  (workplan/dates-from-to min-date max-date {:dates (if by-week? :weeks :months)}))
            )
           )
          (ui-table-body
           {}
           (mapv ui-resource-timesheet resources-ts))))]))))

(comment
  (ui-form
   {}
   (ui-form-group
    {}
    (ui-input
     {:type "month"
      :label "Start"
      :size :mini
      :value (some->> start-date t/date str (take 7) (apply str))
      :onChange (fn [e]
                  (m/set-value! this :resource-ts/start-date (js/Date. (evt/target-value e)))
                  )})
    (ui-input
     {:type "month"
      :label "End"
      :size :mini
      :value (some->> end-date t/instant str (take 7) (apply str))
      :onChange (fn [e]
                  (let [end-date-new (js/Date. (evt/target-value e))]
                    (m/set-value! this :resource-ts/end-date end-date-new)
                    (df/refresh! this {:params {:pathom/context {:resource-ts/start-date start-date
                                                                 :resource-ts/end-date end-date-new}
                                                :by-week? by-week?}})))}))))
