(ns app.ui.projects
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]
   
   
   [app.ui.users :as users]
   [app.ui.workplans :as workplans]
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
   ["react-google-charts" :as ReactGoogleCharts]
   [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.dom.events :as evt]
   [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
   [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
   [com.fulcrologic.fulcro.algorithms.normalize :as normalize]
   [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
   [com.fulcrologic.semantic-ui.elements.flag.ui-flag :refer [ui-flag]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-footer :refer [ui-table-footer]]
   [app.model.item :as item2]
   [com.fulcrologic.rad.type-support.decimal :as math2]
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
   [com.fulcrologic.semantic-ui.elements.button.ui-button-group :refer [ui-button-group]]
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
   [taoensso.timbre :as log :refer-macros [log  trace  debug  info  warn  error  fatal  report
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

(def month-to-number #(get {t/JANUARY 1 t/FEBRUARY 2 t/MARCH 3 t/APRIL 4 t/MAY 5 t/JUNE 6  t/JULY 7  t/AUGUST 8 t/SEPTEMBER 9  t/OCTOBER 10  t/NOVEMBER 11 t/DECEMBER 12} %))
(def ui-number-format (interop/react-factory NumberFormat))
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
  
  (dom/p {} "Welcome :)"))

(def ui-test-c (comp/factory TestC ))

(defn test-c-func [props]
  [(dom/p {} "paragrah")
   (ui-test-c {})])


(def ui-chart (interop/react-factory ReactGoogleCharts/Chart))



(defsc Task [this {:task/keys [name id start-date end-date] :as props}]
  {:query [:task/name :task/id :task/start-date :task/end-date :task/outline-number :task/parent-task-name]
   :ident :task/id}

  (dom/p {} "P")
  )






(defmutation set-due-date [{:keys [ref date]}]
  (action [{:keys [state ref] :as env}]
          
          (ns/update-caller! env  assoc :action/due-date date)
          ))

(defsc ActionForm [this {:action/keys [action owner status due-date ] :ui/keys [new? loading? search-result] :db/keys [id] :as props} ]
  {:query [:db/id :action/action :action/owner :action/status :action/due-date :ui/new? :ui/loading? fs/form-config-join
           :ui/search-result
           [:resource/options2 '_]
           ]
   :ident [:action/id :db/id]
   :initial-state {:ui/search-result [{:title "Sardine"}]}
   #_#_:pre-merge   (fn [{:keys [data-tree]}]
                      
                      (fs/add-form-config ActionForm data-tree))
                                        ;   :form-fields #{:action/action :action/owner :action/status :action/due-date}

   }
  
  (let [suggestions (get props :resource/suggestions)
        ]
    
    
    (ui-container {:fluid true}
                  (ui-form {}
                           (ui-form-group {}
                                          (ui-form-input  {:label "Action" :type "input"
                                                           :error (= :invalid (project/action-validator props :action/action))
                                                           :value action
                                                           
                                                           :onChange #(m/set-string! this :action/action :event %)
                                                           })
                                          ;; todo add dropdown

                                          #_(ui-form-field {}
                                                           (dom/label {} "Owner")
                                                           (ui-dropdown {:search true :selection true :placeholder "Owner"
                                                                         :onBlur (fn [x y] (m/set-string! this :action/owner :value (evt/target-value x)))
                                                                         :onChange #(m/set-string! this :action/owner :value (str (.-value %2)))
                                        ; :onClick #(js/console.log "X" %1 "Y" %2)
                                        ;:onSearchChange #(m/set-string! this :action/owner :value (str (.-value %2))) 
                                        ;:options options2
                                                                         :selectOnBlur false
                                        ;:searchQuery owner
                                                                         :noResultsMessage ""
                                        ;:value owner
                                        ; :searchQuery owner
                                                                         }
                                                                        ))


                                          
                                          

                                          #_(ui-form-input  {:label "Owner" :type "input" 
                                                             :value owner
                                                             :error (= :invalid (project/action-validator props :action/owner))
                                                             :onChange #(m/set-string! this :action/owner :event %)})

                                          (ui-form-field {}
                                                         (dom/label {} "Owner")
                                                         (ui-search {:icon (ui-icon {})
                                                                     :results search-result
                                                                     :selectFirstResult false
                                                                     :onResultSelect #(m/set-string! this :action/owner :value (-> %2 (.-result) (.-title)))
                                                                     :showNoResults false
                                                                     :onSearchChange (fn [x y]
                                                                                       
                                                                                       (let [val (.-value y)
                                                                                             filtred-result (filter (fn [x] (str/includes? (str/lower-case (:title x)) (str/lower-case val))) suggestions)]
                                                                                         (m/set-string! this :action/owner :value (.-value y))
                                                                                         (m/set-value! this :ui/search-result filtred-result)))
                                                                     :value owner}))

                                          (ui-form-field { }
                                                         (dom/label {} "Status")
                                                         (ui-dropdown  {:label "Status"
                                                                        :search true
                                                                        :selection true
                                                                        :value status
                                                                        :options [{:text "Open" :value :open} {:text "Closed" :value :closed} {:text "Cancelled" :value :cancelled}]
                                                                        :onChange #(m/set-value! this :action/status (keyword (.-value %2)))}))

                                          (ui-form-field {}
                                                         (dom/label 
                                                          "Due Date")
                                                         (dom/input {:type "date" :size "mini"  
                                                                     :onChange (fn [x y]
                                        ;(m/set-value! this :action/due-date (js/Date. (.-value (.-target %))))
                                                                                 (comp/transact! this [(set-due-date {:date (js/Date. (.-value (.-target x))) :ref [:action/id id]})] {:refresh []}))
                                                                     :value (apply str (take 10 (str (t/instant due-date))))}))
                                          )
                           (ui-form-group {}
                                          )
                           #_(ui-button {:basic true :onClick (fn [] (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                                                       (comp/transact! this [(project/try-save-action {:db/id id :diff diff}) ]))) }
                                        "Save")))))

(def ui-action-form (comp/factory ActionForm {:keyfn :db/id}))


(defsc Project
  [this {:project/keys [id name]}]
  {:query [:project/id :project/name :project/start-date  :project/last-published-date :project/modified-date :project/finish-date]
   :ident :project/id})

(defsc ActionListLabel [this {:action-list-label/keys [count overdue?]}]
  {:query [:action-list-label/count :action-list-label/overdue? :tempids]
   :initial-state {:action-list-label/count :param/count :action-list-label/overdue? :param/overdue?}}

  (ui-label {:circular true :color (if overdue? "red" "grey") :style {:position "relative" :top "-3px"} } (if (nil? count) 0 count)))

(defsc ProjectPanelQ [this {:keys [  ] :as props}]
  {:query           [ :project-panel/id
                     {:>/action-list-label (comp/get-query ActionListLabel)}
                     {:>/current-project (comp/get-query Project)}
                     :ui/active-item]
   :ident        (fn [] [:component/id :project-panel])
   
   
   
   })


(defsc ProjectPanelQ2 [this {:keys [  ] :as props}]
  {:query           [ :project-panel/id
                     {:>/action-list-label (comp/get-query ActionListLabel)}
                     ]
   :ident        (fn [] [:component/id :project-panel])
   
   
   
   })


(defsc ActionRow [this {:action/keys [action owner status due-date] :db/keys [id] :ui/keys [new? saving? modal-open?]  :as props} {:keys [remove-action save-action load-action-label]} ]
  {:query [:db/id :action/action :action/owner :action/status :action/due-date ;fs/form-config-join
           :ui/new?
           :ui/saving?
           :ui/search-result
           :ui/modal-open?
           [:resource/suggestions '_]
           fs/form-config-join]
   :ident [:action/id :db/id]
   :form-fields #{:action/action :action/owner :action/status :action/due-date}
   :pre-merge   (fn [{:keys [data-tree]}]
                  
                  (fs/add-form-config ActionRow data-tree))
                                        ;   :form-fields #{:action/action :action/owner :action/status :action/due-date}
   :initial-state {:action/owner :param/owner :action/action :param/action :db/id :param/id :action/status :param/status :action/due-date (t/now) :ui/modal-open? false :ui/new? false :ui/saving? false}}
  
  (let [options props]
    
    (ui-modal {:trigger (ui-table-row {:onClick #(m/set-value! this :ui/modal-open? true)}
                                      (ui-table-cell {} action)
                                      (ui-table-cell {} owner)
                                      (ui-table-cell {} (some-> status name clojure.string/capitalize))
                                      ;; TODO 

                                      (ui-table-cell {:error (t/< due-date (t/inst (t/now)))} (apply str (take 10 (str (t/instant due-date)))))
                                      (ui-table-cell {} (ui-icon {:name "times"
                                                                  :basic true
                                                                  :onClick (fn []
                                                                             (if (js/confirm  "Are you sure?")
                                                                               (do (remove-action id)
                                                                                   (load-action-label))
                                                                               (m/set-value! this :ui/modal-open? false)))})))
               :open modal-open?
                                        ;:closeIcon true
               :onClose #(m/set-value! this :ui/modal-open? false)
               }
              (ui-modal-content {} (ui-action-form props))
              (ui-modal-actions {} [(ui-button {:basic true :onClick (fn [] (if new?
                                                                              (remove-action id)
                                                                              (comp/transact! this [(fs/reset-form! {})]))
                                                                       (m/toggle! this :ui/modal-open?)) }
                                               "Undo")
                                    
                                    (ui-button {:basic true :loading saving?  :onClick (fn [] (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                                                                                (when (seq diff)
                                                                                                  (save-action id diff)
                                                                                                  (load-action-label)))) }
                                               "Save")
                                    
                                    
                                    ])
              ))
  )

(def ui-action-row (comp/factory ActionRow {:keyfn :db/id}))

(defn table-cell-field [this field {:keys [prefix onChange validation-message input-tag value-xform type]}]
  (let [props         (comp/props this)
        value         (get props field "")
        input-factory (or input-tag dom/input)
        xform         (or value-xform identity)]
    (td
     (input-factory (cond-> {:prefix prefix
                             :value    (xform value)
                             :onChange (fn [evt] (when onChange
                                                   (onChange evt)))}
                      type (assoc :type type)))
     (div :.ui.up.pointing.red.basic.label
          {:classes [(when (not= :invalid (project/order-validator props field)) "hidden")]}
          (or validation-message "Invalid value")))))



(defn ui-decimal-input
  "Render a money input component. Props can contain:
  :value - The current controlled value (as a bigdecimal)
  :onChange - A (fn [bigdec]) that is called on changes"
  [{:keys [prefix
           value
           onBlur
           onChange]}]
  (let [attrs {:thousandSeparator true
               :prefix            (or prefix "$")
               :value             (math/bigdec->str value)
               :onBlur            (fn [] (when onBlur (onBlur)))
               :onValueChange     (fn [v]
                                    (let [str-value (.-value v)]
                                      (when (and (seq str-value) onChange)
                                        (onChange (math/bigdecimal str-value)))))}]
    (ui-number-format attrs)))


(defsc Order [this {:order/keys [name id currency days amount] :ui/keys [new? saving?] :as props} {:keys [remove-order save-order]}]
  {:query [:order/name :order/id :order/currency :order/days :order/amount :ui/new? :ui/saving?
           fs/form-config-join]
   :ident :order/id
   :pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config Order data-tree))
   :form-fields #{:order/name :order/days :order/amount :order/currency :order/id}}
  (ui-table-row
   {}
   
   (ui-table-cell
    {}

    (table-cell-field this :order/name {:validation-message "Name must not be empty"
                                        :input-tag ui-input
                                        :onChange           #(m/set-string! this :order/name :event %)})
    #_(ui-form-input  {:type "input"
                       #_#_:error (= :invalid (project/action-validator props :action/action))
                       :value name
                       
                       :onChange #(m/set-string! this :order/name :event %)
                       }))
   (ui-table-cell
    {:textAlign :right :singleLine true}
    (ui-dropdown
     {:compact true :basic true 
      :options [{:text "EUR" :value :currency/EUR}
                {:text "CAD" :value :currency/CAD}
                {:text "USD" :value :currency/USD}]

      ;; TODO handle this on the server 
      :value  currency
      :onChange (fn [event data]
                  (comp/transact!
                   this
                   [(project/set-order-currency {:value (keyword (str "currency/" (.-value data))) :id id})]))
      }))

   
   

   (table-cell-field this :order/days {:validation-message "Amount must be a positive amount."
                                       :input-tag          ui-decimal-input
                                       :prefix ""
                                       :onChange           #(m/set-value! this :order/days %)})
   (table-cell-field this :order/amount {:validation-message "Days must be a positive amount."
                                         :input-tag          ui-decimal-input
                                         :prefix "$"
                                         :onChange           #(m/set-value! this :order/amount %)})
   #_(ui-table-cell {} amount)
   (ui-table-cell
    {}
    (let [visible? (or new? (fs/dirty? props))]
      (if visible? 
        (ui-button-group
         {:basic true}
         (ui-button {:icon "save outline"
                     :disabled (= :invalid project/order-validator props)
                     :onClick  (fn []
                                 (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                   (save-order id diff)))
                     }
                    )
         (ui-button {:icon "undo" :onClick
                     (fn [] (if new?
                              (remove-order id)
                              (comp/transact! this [(fs/reset-form! {})])))})
         (ui-button {:icon "times" :onClick #(remove-order id)}))
        (ui-button-group
         {:basic true}
         
         
         (ui-button {:icon "times" :onClick #(remove-order id)}))

        
        )
      
      ))


   #_(td
      (let [visible? (or new? (fs/dirty? props))]
        (when visible?
          (div :.ui.buttons
               (button :.ui.inline.primary.button
                       {:classes  [(when saving? "loading")]
                                        ;:disabled (= :invalid (item/item-validator props))
                        :onClick  (fn []
                                    (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                      #_(comp/transact! this [(item/try-save-item {:item/id id :diff diff})])))} "Save")
               (button :.ui.inline.secondary.button
                       
                       "Undo")))))))

(def ui-order (comp/factory Order {:keyfn :order/id}))



(defsc Finance [this {:finance/keys [id orders] :as props}]
  {:query [:finance/id {:finance/orders (comp/get-query Order)}]
   :route-segment  ["finance" :finance/id]
   :ident  :finance/id
   :initial-state {:finance/orders []}
   :will-enter (fn [app {:keys [finance/id] :as params}]
                 (dr/route-deferred
                  [:finance/id (uuid id)]
                  (fn []
                    (df/load! app [:finance/id (uuid id)] Finance
                              {:marker :order-marker})
                    #_(merge/merge-component! app Finance {:finance/id (uuid id) :finance/orders [{:ui/new? true
                                                                                                   :order/id (random-uuid)
                                                                                                   :order/name "N"
                                                                                                   :order/currency :currency/CAD
                                                                                                   :order/days 0
                                                                                                   :order/amount 0}]})
                    (comp/transact! app [(dr/target-ready {:target [:finance/id (uuid id)]})]))))}

  (let [
        remove-order (fn [orderid] (comp/transact! this [(project/remove-order {:order/id orderid :finance id})]))
        save-order (fn [dbid diff] (comp/transact! this [(project/try-save-order {:order/id dbid :diff diff :finance id})]))
        revenue (apply math/+ (mapv :order/amount orders))
        work  (apply math/+ (mapv :order/days orders))
        adr (if (> work 0) (math/div revenue work) 0)]
    (js/console.log "revenue" revenue)
    (ui-container {}
                  (ui-segment {:color :violet}
                              
                              (ui-header {:textAlign :center :color "blue"} "Orders")
                              
                              (ui-grid {}

                                       (ui-grid-column {:width 14}
                                                       (ui-table {:color :blue :striped true :celled true :style {:fontSize "85%"}}
                                                                 (ui-table-header {} (ui-table-row {} (map #(ui-table-header-cell {} %)
                                                                                                           ["Name" "Currency" "Days" "Amount" "Actions"])))
                                                                 
                                                                 (ui-table-body
                                                                  {}
                                                                  (map (fn [order] (ui-order
                                                                                    
                                                                                    (comp/computed order {:remove-order remove-order :save-order save-order}) )) orders))

                                                                 (ui-table-footer {} (ui-table-row {:textAlign :right}
                                                                                                   (ui-table-header-cell {:colSpan 5}
                                                                                                                         (ui-icon {:basic true
                                                                                                                                   :onClick  (fn []
                                                                                                                                               (let [random-id (random-uuid)]

                                                                                                                                                 (merge/merge-component!
                                                                                                                                                  SPA Order
                                                                                                                                                  {:ui/new? true
                                                                                                                                                   :order/id random-id
                                                                                                                                                   :order/name ""
                                                                                                                                                   :order/currency :currency/CAD
                                                                                                                                                   :order/days 0
                                                                                                                                                   :order/amount 0}
                                                                                                                                                  :append [:finance/id id :finance/orders]
                                                                                                                                                  
                                                                                                                                                  ))
                                                                                                                                               #_(comp/transact! this add-order {:id ids})) :name "plus"}
                                                                                                                                  ))))))
                                       (ui-grid-column
                                        {:width 2}
                                        (ui-form
                                         {:style {:fontSize "90%"}}
                                         (ui-form-field
                                          {}
                                          (dom/label {} "Revenue: ")
                                          (dom/span {}  (math/bigdec->str revenue)))
                                         (ui-form-field
                                          {}
                                          (dom/label {} "Work: ")
                                          (dom/span {}  (math/bigdec->str work)))
                                         (ui-form-field
                                          {}
                                          (dom/label {} "ADR: ")
                                          (dom/span {}  (math/bigdec->str adr)))))))
                  #_(ui-segment {:color :blue}
                                (ui-header {:textAlign :center :color "blue"} "Targets")
                                
                                (ui-table {:color :blue :striped true :celled true}
                                          (ui-table-header {} (ui-table-row {} (map #(ui-table-header-cell {} %)
                                                                                    ["Name" "Currency" "Days" "Amount"])))
                                          
                                          (ui-table-body
                                           {}
                                           (map (fn [order] (ui-order
                                                             (comp/computed order {:remove-order remove-order :save-order save-order}) )) orders))

                                          (ui-table-footer {} (ui-table-row {:textAlign :right}
                                                                            (ui-table-header-cell {:colSpan 5}
                                                                                                  (ui-icon {:basic true
                                                                                                            :onClick  #(merge/merge-component!
                                                                                                                        this Order
                                                                                                                        {:ui/new? true
                                                                                                                         :order/id (random-uuid)
                                                                                                                         :order/name ""
                                                                                                                         :order/currency nil
                                                                                                                         :order/days 0
                                                                                                                         :order/amount 0}
                                                                                                                        :append [:finance/id id :finance/orders])
                                                                                                            :name "plus"}
                                                                                                           )))))))
    
    ))



(defsc ActionList [this {:action-list/keys [id actions] :as props}]
  {:query [:action-list/id {:action-list/actions (comp/get-query ActionRow)}
           [df/marker-table '_]
                                        ;[:resource/options2 '_]
           ]
   
   :route-segment   ["action-list" :action-list/id]
   :ident  :action-list/id
   
   
   :will-enter (fn [app {:keys [action-list/id] :as params}]
                 (dr/route-deferred
                  [:action-list/id (uuid id)]
                  (fn []
                    (df/load! app [:action-list/id (uuid id)] ActionList {:marker :action-list})
                    (comp/transact! app [(dr/target-ready {:target  [:action-list/id (uuid id)]})]))))}

                                        ;(js/console.log "props" props)
  (let [status (get props [df/marker-table :action-list])
                                        ;options(get  props :resource/options2)
        remove-action (fn [dbid] (comp/transact! this [(project/remove-action {:db/id dbid :action-list id})]))
        save-action (fn [dbid diff] (comp/transact! this [(project/try-save-action {:db/id dbid :diff diff :action-list id})]))
        load-action-label (fn [] (df/load! this :project-panel ActionListLabel {:target [:component/id :project-panel :>/action-list-label] :params {:pathom/context {:project/id id}}
                                                                                :referesh [:action-list-label/count :action-list-label/overdue?]}))
        ]
    
    (ui-container {}
                  (if (df/loading? status)
                    (ui-loader {})
                    (ui-table {:color :blue :striped true :celled true}
                              (ui-table-header {} (ui-table-row {} (map #(ui-table-header-cell {} %) ["Action" "Owner" "Status" "Due date" "Delete"])))
                              
                              (ui-table-body {} (map (fn [action] (ui-action-row (comp/computed action {:remove-action remove-action :save-action save-action :load-action-label load-action-label}) )) actions ))
                              (ui-table-footer {} (ui-table-row {:textAlign :right}
                                                                
                                                                (ui-table-header-cell {:colSpan 5}
                                                                                      (ui-icon {:basic true
                                                                                                :onClick  #(merge/merge-component! this ActionRow
                                                                                                                                   {:ui/new? true
                                                                                                                                    :db/id (tempid/tempid)
                                                                                                                                    :action/action ""
                                                                                                                                    :action/owner ""
                                                                                                                                    :action/status :open
                                                                                                                                    :action/due-date (-> t/now t/inst)}
                                                                                                                                   :append [:action-list/id id :action-list/actions])
                                                                                                :name "plus"}
                                                                                               )))))))
    
    ))


(defsc WorkPlan [this {:workplan/keys [id] :as props}]
  {:query [:workplan/id 
           [df/marker-table '_]
           ]
   
   :route-segment   ["workplan" :workplan/id]
   :ident  :workplan/id
   
   
   :will-enter (fn [app {:keys [action-list/id] :as params}]
                 (dr/route-deferred
                  [:workplan/id (uuid id)]
                  (fn []
                    #_(df/load! app [:action-list/id (uuid id)] ActionList {:marker :action-list})
                    (merge/merge-component! app WorkPlan {:workplan/id (uuid id)})
                    (comp/transact! app [(dr/target-ready {:target  [:workplan/idd (uuid id)]})]))))}

                                        ;(js/console.log "props" props)
  (let [status (get props [df/marker-table :action-list])
                                        ;options(get  props :resource/options2)
        remove-action (fn [dbid] (comp/transact! this [(project/remove-action {:db/id dbid :action-list id})]))
        save-action (fn [dbid diff] (comp/transact! this [(project/try-save-action {:db/id dbid :diff diff :action-list id})]))
        load-action-label (fn [] (df/load! this :project-panel ActionListLabel {:target [:component/id :project-panel :>/action-list-label] :params {:pathom/context {:project/id id}}
                                                                                :referesh [:action-list-label/count :action-list-label/overdue?]}))
        ]
    
    (ui-container {}
                  (dom/div {} "X"))
    ))




(defsc TimeLine [this {:keys [timeline/tasks-level2 timeline/tasks-level3] :as props}]
  {:query [:timeline/id {:timeline/tasks-level2 (comp/get-query Task) } {:timeline/tasks-level3 (comp/get-query Task)}]
   
   :route-segment   ["timeline" :timeline/id]
   :ident  :timeline/id 
                                        ;:initial-state {:project/id 1 }
   :will-enter (fn [app {:keys [timeline/id] :as params}]
                 (dr/route-deferred
                  [:timeline/id (uuid id)]
                  (fn []
                                        ;(df/load! app [:project-info/id (uuid id)] ProjectInfo)
                                        ;(merge/merge-component! app TimeLine { (uuid id)})
                    (df/load! app [:timeline/id (uuid id)] TimeLine)
                    (comp/transact! app [(dr/target-ready {:target  [:timeline/id (uuid id)]})]))))}

                                        ;(js/console.log "props" props)
  (let [options (get props :resource/options2)]
    #_(js/console.log "PPPPPPP" (reverse (conj (mapv (fn [t] (vals (select-keys t [:task/parent-task-name :task/name :task/start-date :task/end-date]))) tasks-level3)
                                               [{:type :string :id :parent}
                                                {:type :string :id :name}
                                        ;{:type :string :id :te}
                                                {:type :date :id :start}
                                                {:type :date :id :end}])))
    ;; level 2 
    (ui-chart {:height "1000px" :width "100%" :chartType "Timeline" :data (reverse (conj (mapv (fn [t] (vals (dissoc t :task/id :task/outline-number))) tasks-level2)
                                                                                         [{:type :string :id :name}
                                        ;{:type :string :id :te}
                                                                                          {:type :date :id :start}
                                                                                          {:type :date :id :end}]))})


    ;; level3 
    #_(ui-chart {:height "1000px" :width "100%" :chartType "Timeline" :data (reverse (conj (mapv (fn [t] (vals (select-keys t [:task/parent-task-name :task/name :task/start-date :task/end-date]))) tasks-level3)
                                                                                           [{:type :string :id :parent}
                                                                                            {:type :string :id :name}
                                        ;{:type :string :id :te}
                                                                                            {:type :date :id :start}
                                                                                            {:type :date :id :end}]))})
    ))


#_(defsc TimeLine [this props]
    {:route-segment ["timeline"]}
    (dom/p {} "TimeLine"))


(defn ui-project-governance-review [props]
  
  (ui-tab-pane {}
               (ui-container {:textAlign "center"}
                             )))

                                        ;(def ui-project-governance-review (comp/factory ProjectGovernanceReview))

(defmutation change-route [{:keys [this target]}]
  (action [{:keys [state]}
           ]
          
          (dr/change-route this target)))










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
                 
                 (dr/route-deferred
                  [:gov-review-week/week week]
                  (fn []
                    #_(merge/merge-component! app GovReviewWeek {:gov-review-week/week week})
                    
                    (comp/transact! app [(dr/target-ready {:target [:gov-review-week/week week]})]))))
                                        ;:pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config GovReviewWeek data-tree))
   :intial-state {}}
  
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
                          (when (and (not (keyword? (:resource/email-address submitted-by)))
                                     (not (nil? (:resource/email-address submitted-by))))
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

(defmutation close-new-modal [{:keys [new-modal-id action-list-id]}]
  (action [{:keys [state]}]
          (swap! state #(assoc-in % [:action/id  new-modal-id :ui/modal-open?] false))))




(defsc GovReview [this {:keys [ gov-review/new-action gov-review/id gov-review/current-weeks gov-review/current-week ui/modal-open?] :as props}]
  {:query [:ui/modal-open? :gov-review/id {:gov-review/current-weeks (comp/get-query GovReviewWeek)} {:gov-review/current-week (comp/get-query GovReviewWeek)}
           {:gov-review/new-action (comp/get-query ActionRow)} 
           ]
   :route-segment ["gov-review" :gov-review/id]
   :ident  (fn [] [:component/id :gov-review])
   :initial-state (fn [p] {:ui/modal-open? false :gov-review/current-weeks [[:gov-review-week/week (t/instant (t/now))][:gov-review-week/week (t/instant (t/now))] [:gov-review-week/week (t/instant (t/now))] [:gov-review-week/week (t/instant (t/now)) :gov-review/new-action {}]]})
   :will-enter (fn [app {:keys [gov-review/id] :as params}]
                 (dr/route-deferred
                  
                  [:component/id :gov-review]
                  (fn []
                    #_(df/load! app [:project-info/id id] ProjectInfo)
                    

                    
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
                        
                        #_(merge/merge-component! SPA GovReviewWeek {:gov-review-week/week (first weeks)
                                                                     :gov-review-week/status :open} :replace [:component/id :gov-review :gov-review/current-weeks index]
                                                                     )
                        (comp/transact! app [(project/get-or-create-gov-review-week {:gov-review-week/week (first weeks) :index index :project/id (uuid (:gov-review/id params))})])
                        
                        (recur (rest weeks) (inc index) )))

                    
                    (comp/transact! app [(dr/target-ready {:target [:component/id :gov-review]})])
                    
                    )               ))}

  
  
  (let [current-tab (some-> (dr/current-route this this) first keyword)]
    (ui-container {:textAlign :center :style {:fontSize "85%"}}

                  (dom/div {:style {:display "flex" :alignItems "center" :alignContent "center" :flexWrap "nowrap"  }}
                           (ui-icon { :onClick (fn [e]
                                                 (let [last-week (:gov-review-week/week (first current-weeks))
                                                       previous-weeks (previous-weeks last-week)]


                                                   (loop [weeks previous-weeks
                                                          index 0]
                                                     
                                                     (when (seq weeks)
                                                       
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
                           (ui-step-group {:style {} :fluid true  :size :mini}
                                          
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
                           
                           (ui-icon {:style {:position "relative" :left "3px"} :name "chevron right" :onClick (fn [e]
                                                                                                                (let [last-week (:gov-review-week/week (last current-weeks))
                                                                                                                      next-weeks (next-weeks last-week)]


                                                                                                                  (loop [weeks next-weeks
                                                                                                                         index 0]
                                                                                                                    
                                                                                                                    (when (seq weeks)
                                                                                                                      
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
                  
                  (ui-gov-review-week current-week)

                  #_(ui-dropdown{:text "Add"
                                 :options [{:text "Action" :value :add-action} {:text "Risk" :value :add-risk}]
                                 :onChange (fn [x y]
                                             
                                             (m/toggle! this :ui/modal-open?)
                                             (let [new-action-row (tempid/tempid)]
                                               (merge/merge-component! this ActionRow
                                                                       {:ui/new? true
                                                                        :db/id (tempid/tempid)
                                                                        :action/action ""
                                                                        :action/owner ""
                                                                        :action/status :open
                                                                        :action/due-date (-> t/now t/inst)}
                                                                       :append [:action-list/id id :action-list/actions]
                                                                       :replace [:component/id :gov-review :gov-review/new-action])))})
                  #_(ui-menu {#_#_:vertical true :size :tiny :compact true :borderless true   :basic true
                              :style {:paddingBottom "0px" :position "relative ":top "-2px" :selfAlign :right
                                      :border "0.5px solid #dedede"}
                              }


                             (ui-dropdown {:item true :text "Add" :style {:border "5px"}}
                                          (ui-dropdown-menu {:style {:border "1px"}}
                                                            (ui-dropdown-item {:onClick (fn [e d]
                                                                                          (m/toggle! this :ui/modal-open?)
                                                                                          (merge/merge-component! this ActionRow
                                                                                                                  {:ui/new? true
                                                                                                                   :db/id (tempid/tempid)
                                                                                                                   :action/action ""
                                                                                                                   :ui/modal-open? true
                                                                                                                   :action/owner ""
                                                                                                                   :action/status :open
                                                                                                                   :action/due-date (-> t/now t/inst)}
                                                                                                                  :append [:action-list/id id :action-list/actions]
                                                                                                                  :replace [:component/id :gov-review :gov-review/new-action])) } "Action")
                                                            #_(ui-dropdown-item {} "Risk"))))

                  (ui-grid-row
                   {}
                   (ui-button  {:basic true :style {:marginLeft "25px" } :onClick (fn [e d]
                                                                                    (m/toggle! this :ui/modal-open?)
                                                                                    (merge/merge-component! this ActionRow
                                                                                                            {:ui/new? true
                                                                                                             :db/id (tempid/tempid)
                                                                                                             :action/action ""
                                                                                                             :ui/modal-open? true
                                                                                                             :action/owner ""
                                                                                                             :action/status :open
                                                                                                             :action/due-date (-> t/now t/inst)}
                                                                                                            :append [:action-list/id id :action-list/actions]
                                                                                                            :replace [:component/id :gov-review :gov-review/new-action]))
                                } "Add Action")

                   
                   


                   (ui-button  {:basic true :style {:marginLeft "25px" } :onClick (fn [e] (comp/transact! this [(project/submit-current-gov-review-week {:gov-review-week current-week :project-info/id id})]))} "Submit"))
                  
                  (ui-modal {:open (:ui/modal-open? new-action) :onClose #(comp/transact! this [(close-new-modal {:action-id (:db/id new-action) :action-list-id id})])}
                            (ui-modal-content {} (ui-action-form new-action))
                            (ui-modal-actions {} [(ui-button {:basic true :onClick (fn [] (comp/transact! this [(project/remove-action {:db/id (:db/id new-action) :action-list id})])
                                                                                     (m/toggle! this :ui/modal-open?)) }
                                                             "Cancel")
                                                  
                                                  (ui-button {:basic true  :onClick (fn [] (let [diff (fs/dirty-fields new-action false {:new-entity? true})]

                                                                                             (comp/transact! this [(project/try-save-action {:db/id (:db/id new-action) :diff diff :action-list id})])
                                                                                             
                                                                                             )) }
                                                             "Save")
                                                  
                                                  
                                                  ]))
                  
                  

                  

                  
                  
                  )))





(defsc ProjectInfo2 [this props]
  {:route-segment ["project-info2"]}

  (dom/p {} "SOMETHING2")
  )
(def ui-project-info2 (comp/factory ProjectInfo2))

(defsc ProjectInfo [this {:project-info/keys [start-date modified-date finish-date id new  name    finish-date project-lead functional-lead
                                              technical-lead status phase entity fluxod-name fluxod-project-names fluxod-client-name
                                              last-published-date]
                          
                          :ui/keys [loading? new-fluxod-project-name] :as props}]
  {:query [:project-info/id :project-info/modified-date :project-info/start-date  :project-info/finish-date
           :project-info/last-published-date
           :ui/new-fuxod-project-name
           :project-info/last-published-date :project-info/finish-date :project-info/name :project-info/status
           :project-info/fluxod-client-name
           :project-info/phase
           :project-info/fluxod-project-names
           :ui/new-fluxod-project-name
           :project-info/entity
           :project-info/new
           :ui/loading?
           :project-info/fluxod-name
           [:resource/options2 '_]
           [df/marker-table '_]
           
           fs/form-config-join
           {:project-info/project-lead (comp/get-query users/Resource)} {:project-info/functional-lead (comp/get-query users/Resource)} {:project-info/technical-lead (comp/get-query users/Resource)}]
   
   :route-segment   ["project-info" :project-info/id]
   :ident   (fn [] [:project-info/id id])
   :initial-state (fn [p] {:ui/loading true :project-info/fluxod-project-names []})
   #_#_:pre-merge (fn [{:keys [current-normalized data-tree]}]
                    (merge current-normalized
                           (assoc data-tree :project-info/fluxod-project-names ["A" "B" "C" "D"])))
   :will-enter (fn [app {:keys [project-info/id] :as params}]
                 
                 (dr/route-deferred
                  [:project-info/id (uuid id)]
                  (fn []
                    
                    (df/load!
                     app
                     [:project-info/id (uuid id)]
                     ProjectInfo
                     {:marker :project-info :post-mutation `dr/target-ready :post-mutation-params {:target [:project-info/id (uuid id)]}})
                    
                    
                    

                    )))}

                                        ;(js/console.log "props" props)
  (let [load-status (get props df/marker-table)
        str-date #(some->> % t/date-time str (take 16) (apply str))
        options (get props :resource/options2)]
    
    
    
    (ui-grid-column
     {}
     
     (ui-form
      {}
      (ui-form-group
       {}
       #_(ui-form-field {:width 6}(ui-form-input {:label "Name" :placeholder "Project Name" :readOnly true :value name})))

      (ui-form-group
       {}
       (ui-form-field
        {}
        (dom/label
         {} "Project Status")
        (ui-dropdown
         {:placeholder "Project Status"
          :selection true
          :search true
          :options  
          [{:text "Sales" :value :sales}
           {:text "In Progress" :value :in-progress}
           {:text "Closed" :value :closed}
           {:text "Cancelled" :value :cancelled}]
          :value status
          
          :onChange (fn [e d]
                      
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
                                      (str-date last-published-date )

                                      :onChange
                                      (fn [e d] )})
                     
                     (ui-form-input  {:label "Start Date" :type "datetime-local" :readOnly true
                                      :value (str-date start-date)
                                      })
                     (ui-form-input  {:label "End Date" :type "datetime-local" :readOnly true
                                      :value
                                      (str-date finish-date)
                                      
                                      }))
      (ui-divider {})

      
      (ui-form-group {}


                     (ui-form-field {}
                                    (dom/label {} "Project Lead")
                                    (ui-dropdown {:placeholder "Project Lead"
                                                  :selection true
                                                  :search true
                                                  :options  options
                                                  :value (:resource/id project-lead)
                                                  
                                                  :onChange (fn [e d]
                                                              
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
                                                  :onChange (fn [a b](comp/transact! this [(project/set-technical-lead {:lead-id (.-value b) :project-info/id id } )])
                                                              
                                                              )
                                                  
                                                  })

                                    
                                    
                                    )

                     

                     )

      ;; TOOD 
      (ui-grid
       {:centered true}
       (ui-grid-row
        {:columns 2 :stretched true}
        (ui-grid-column
         {}
         (ui-form-field
          {}
          (dom/label {} "Fluxod Client Name")
          (ui-input {:placeholder "Fluxod Client Name"
                     :value fluxod-client-name
                     :onChange #(m/set-string! this :project-info/fluxod-client-name :event %)
                     :onBlur #(comp/transact! this [(project/save-fluxod-client-name {:name fluxod-client-name :id id})])})))
        (ui-grid-column
         {}
         (ui-form-field
          {}
          (dom/label {} "Fluxod Project Name")
          (ui-input {:placeholder "Fluxod Project Name "
                     :value new-fluxod-project-name
                     :onChange (fn [e]
                                 (m/set-string! this
                                                :ui/new-fluxod-project-name
                                                :event
                                                e))
                     :onKeyDown (fn [e]
                                  (when (evt/enter? e)
                                    (comp/transact! this [(project/add-fluxod-project-names
                                                           {:new-name new-fluxod-project-name
                                                            :project-info/id id})])))})
          (when (seq fluxod-project-names)
            (ui-table
             {}
             (map #(ui-table-row
                    {:textAlign :left}
                    (ui-table-cell {}  (str  % ))
                    (ui-table-cell {:textAlign :right
                                    }
                                   (ui-icon {:name "x"
                                             :onClick (fn [_]
                                                        (comp/transact! this [(project/remove-fluxod-project-name {:name %  :id id})]))}) ))  fluxod-project-names ))))
         )
        
        
        ))
      ))))



(declare ProjectPanel)
(declare AdminProjects)

(form/defsc-form AccountForm [this props]
  {::form/id                account/id
   ::form/attributes        [account/name2]
   ::form/cancel-route      (dr/path-to ProjectInfo)
   ::form/route-prefix      "address"})



(form/defsc-form LineItemForm [this props]
  {::form/id           line-item/id
   ::form/attributes   [line-item/item line-item/quantity]
   ::form/cancel-route ["landing-page"]
   ::form/route-prefix "line-item"
   ::form/layout       [[:line-item/item :line-item/quantity]]
   #_#_::form/subforms     {:line-item/item {::form/ui       form/ToOneEntityPicker
                                         ::form/pick-one {:options/query-key :item/all-items
                                                          :options/subquery  [:item/id :item/name :item/price]
                                                          :options/transform (fn [{:item/keys [id name price]}]
                                                                               {:text (str name " - " (math2/numeric->currency-str price)) :value [:item/id id]})}}}})

(defsc RiskIssues [this {:keys [] :as props}]
  {:query []
   :ident (fn [] [:component/id :risk-issues])
   :route-segment ["risk-issues"]
   :initial-state {}}
  (ui-container {:text true}
                (ui-segment {:textAlign :center} "Comming soon.")))


(dr/defrouter ProjectPanelRouter
  [this {:keys [route-factory route-props current-state] :as props}]
  {:router-targets [ProjectInfo workplans/WorkPlan2  GovReview TimeLine ActionList AccountForm Finance]
   :always-render-body? true}
  (div :.container
       ;; Show an overlay loader on the current route when we're routing
       (when (not= :routed current-state)
         (div :.ui.active.inverted.dimmer
              (div :.ui.text.loader
                   "Loading...")))
       (when route-factory
         (route-factory (comp/computed route-props (comp/get-computed this))))))

(def ui-project-panel-router (comp/factory ProjectPanelRouter))

(defmutation set-current-project-id [{:keys [current-project-id]}]
  (action [{:keys [state app]}]
          (let [ ]
            
            (swap! state (fn [state]
                           (-> state
                               (assoc-in [:component/id :project-panel :project-panel/current-project-id] current-project-id))))
            (dr/target-ready! app [:component/id :project-panel])
            ;; (swap! state  merge/merge-component TeamCheckbox
            ;;        {:db/id team-id :team/name name }
            
            ;;        )
            ))

  
  )




(def ui-action-list-label (comp/factory ActionListLabel))




(defsc ProjectPanel [this {:keys [>/current-project ui/active-item >/action-list-label project-panel/router  project-panel/id] :as props}]
  {:query           [:project-panel/id { :project-panel/router (comp/get-query ProjectPanelRouter) } {:>/current-project (comp/get-query Project) }
                     :project-panel/id
                     [::uism/asm-id '_]
                     {:>/action-list-label (comp/get-query ActionListLabel)}
                     :ui/active-item]
   :ident        (fn [] [:component/id :project-panel])
   :initial-state {:project-panel/router {} :ui/active-item :info }
   :route-segment   ["project-panel" :project-panel/current-project-id]
                                        ;:form-fields #{:project-panel/project-lead :project-panel/functional-lead :project-panel/technical-lead}
                                        ;:pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config ProjectPanel data-tree))
   
   
   :will-enter (fn [app {:keys [project-panel/current-project-id] :as params}]
                 
                 (dr/route-deferred
                  [:component/id :project-panel]
                  (fn []
                    
                    (df/load! app  :project-panel ProjectPanelQ {:taget [:component/id :project-panel]
                                                                 :params {:pathom/context {:project/id (uuid current-project-id)}}
                                                                 :post-mutation `dr/target-ready
                                                                 :post-mutation-params {:target [:component/id :project-panel]}})
                                        ;(comp/transact! app [(dr/target-ready {:target [:project-panel/id (uuid current-project-id)]})])
                                        ;(comp/transact! app [(set-current-project-id {:current-project-id current-project-id})])
                    )))

   
   #_#_:componentDidMount (fn [this]
                            
                            )}

  
  (let [current-project-id (:project/id current-project)]
    
    [(dom/h3 {:style {:color "#3281b9"}} (:project/name current-project))
     (ui-grid-column {:width 4} 
                     (ui-menu {:fluid true :vertical true :tabular true}
                              (ui-menu-item {:name "Info" :active (= active-item :info) :onClick (fn [e]
                                        ;(comp/update-state! this assoc :active-item :info )
                                                                                                   (m/set-value! this :ui/active-item :info)
                                                                                                   (dr/change-route this (dr/path-to ProjectInfo {:project-info/id id})))} )
                              (ui-menu-item {:name "Governance Review" :active (= active-item :governance-review) :onClick (fn []
                                                                                                                             (m/set-value! this :ui/active-item :governance-review)

                                                                                                                             
                                                                                                                             ;; check this out! TODO 
                                                                                                                             (dr/change-route this (dr/path-to  GovReview {:gov-review/id id  } )))} )


                              
                              (ui-menu-item {:name "Action List" :active (= active-item :action-list) :onClick (fn []
                                                                                                                 (m/set-value! this :ui/active-item :action-list)
                                                                                                                 
                                                                                                                 (dr/change-route this (dr/path-to  ActionList {:action-list/id  id} ))
                                                                                                                 )
                                             :icon (ui-action-list-label action-list-label)}
                                            )
                              (ui-menu-item {:name "TimeLine" :active (= active-item :timeline) :onClick (fn []
                                                                                                           (m/set-value! this :ui/active-item :timeline)
                                                                                                           (dr/change-route this (dr/path-to  TimeLine {:timeline/id  id} )) )} )
                              (ui-menu-item {:name "Work Plan" :active (= active-item :workplan) :onClick (fn []
                                                                                                            (m/set-value! this :ui/active-item :workplan)
                                                                                                            (dr/change-route this (dr/path-to  workplans/WorkPlan2 {:workplan/id id} )))} )
                              (ui-menu-item {:name "Finance" :active (= active-item :finance) :onClick (fn []
                                                                                                         (m/set-value! this :ui/active-item :finance)
                                                                                                         (dr/change-route this (dr/path-to Finance {:finance/id  id})))}
                                            )))

     (ui-grid-column {:width 12} 
                     (ui-project-panel-router router))]
    

    

    )
  
  )

#_(defsc GovernanceReview2 [this props]
    {:query []
     :route-segment ["governance-review2" :governance-review/id]
     :ident (fn [] [:component/id :governance-review2])}
    (dom/p {} "GOV REVIEW2"))

(defsc AdminProject [this {:keys [project/id ]}]
  {:query [:project/id :project/name
           {:project-info/project-lead [:resource/id :resource/name]}
           {:project/resources (comp/get-query Resource)}
           :gov-review-week/exec-summary-color
           :gov-review-week/client-relationship-color
           :gov-review-week/finance-color
           :gov-review-week/scope-schedule-color
           :project-info/status]
   :ident (fn [] [:admin-project/id id])})


(defmutation testm [{:keys []}]
  (action [{:keys [state ref app] :as env}]
          (let [Root (comp/registry-key->class :app.ui.root/Root)]
            #_(js/console.log (comp/get-initial-state Root {})))))


(defsc AdminProjects [this {:admin-projects/keys [admin-projects router] :ui/keys [in-progress? cancelled? closed? sales? ui/column ui/direction project-leads]
                            :keys [resource/options2 ]:as props}]
  {:query         [{:admin-projects/admin-projects  (comp/get-query AdminProject)}
                   :ui/project-leads 
                   {:admin-projects/router (comp/get-query ProjectPanelRouter)}
                   [::uism/asm-id ::session/session]

                   :ui/column
                   :ui/direction
                   [:resource/options2 '_]
                   :ui/in-progress?
                   :ui/closed?
                   :ui/sales?
                   :ui/cancelled?

                   [df/marker-table :admin-projects]]
   
   :ident         (fn [] [:component/id :admin-projects])
   :initial-state (fn [p] {:ui/in-progress? true :ui/cancelled? false :ui/closed? false :ui/sales? false :ui/column nil :ui/direction nil
                           })
   :initLocalState (fn [this props]
                     {:active-index -1})
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-projects]
                  (fn []
                    
                    (df/load! app :all-admin-projects AdminProject
                              {:parallel true
                               :target [:component/id :admin-projects :admin-projects/admin-projects]
                               :marker :admin-projects
                               })
                    (comp/transact! app [(dr/target-ready {:target [:component/id :admin-projects]})]))))
   :route-segment ["admin-projects"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   
   
   }

  
  (let [current-project-leads (filter #(get-in [:project-info/project-lead] %) admin-projects)
        sort-by-column (fn [clicked-column]
                         (if (not= column clicked-column)
                           (do
                             (m/set-value! this :ui/column clicked-column)
                             (m/set-value! this :admin-projects/admin-projects
                                           (vec (if (= clicked-column :project/name)
                                                  (sort-by :project/name admin-projects)
                                                  (sort-by  (comp :resource/name :project-info/project-lead) admin-projects))))
                             (m/set-value! this :ui/direction :ascending))
                           (do
                             (m/set-value! this :admin-projects/admin-projects (reverse admin-projects))
                             (m/set-value! this :ui/direction (if (= direction :ascending) :descending :ascending)))))

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
    

    (if logged-in?
      
      
      [(when (df/loading? marker)
         (div :.ui.active.inverted.dimmer
              (div :.ui.text.loader
                   )))
       

       (ui-grid-column  {:width 3 :style {:color "#3281b9"}}

                        
                        (ui-accordion
                         {:as Menu  :vertical true :style {:color "#3281b9"}}
                         
                         (ui-menu-item {}
                                       (ui-accordion-title
                                        {:active (= active-index 1)
                                         :content "Status"
                                         :index 1
                                         :onClick handleClick
                                         :style {:color "#3281b9"}})
                                       (ui-accordion-content
                                        {:active (= active-index 1)
                                         :content (ui-form
                                                   {}
                                                   (ui-form-group
                                                    {:grouped true}
                                                    
                                                    (ui-form-field
                                                     {}                                                                                                                   
                                                     (dom/div :.ui.checkbox
                                                              (dom/input
                                                               {
                                                                :type "Checkbox"
                                                                :checked  in-progress?
                                                                :style {:padding "10px"}
                                                                :onChange (fn [_ d]
                                                                            (m/toggle! this :ui/in-progress?))})
                                                              (dom/label {:style {:color "#3281b9" }} "In Progress")))
                                                    (ui-form-field {}                                                                                                                   
                                                                   (dom/div :.ui.checkbox
                                                                            (dom/input
                                                                             {
                                                                              :type "Checkbox"
                                                                              :checked sales?
                                                                              
                                                                              :style {:padding "10px"}
                                                                              :onChange (fn [_ d]
                                                                                          (m/toggle! this :ui/sales?))})
                                                                            (dom/label {:style {:color "#3281b9" }} "Sales")))
                                                    (ui-form-field
                                                     {}                                                                                                                   
                                                     (dom/div :.ui.checkbox
                                                              (dom/input
                                                               {
                                                                :type "Checkbox"
                                                                :checked closed?
                                                                
                                                                :style {:padding "10px"}
                                                                
                                                                :onChange (fn [_ d]
                                                                            (m/toggle! this :ui/closed?))})
                                                              (dom/label
                                                               {:style {:color "#3281b9" }} "Closed")))
                                                    (ui-form-field
                                                     {}                                                                                                                   
                                                     (dom/div :.ui.checkbox
                                                              (dom/input
                                                               {
                                                                :type "Checkbox"
                                                                :checked cancelled?
                                                                
                                                                :style {:padding "10px"}
                                                                
                                                                :onChange (fn [_ d]
                                                                            (m/toggle! this :ui/cancelled?))})
                                                              (dom/label
                                                               {:style {:color "#3281b9" }} "Cancelled")))
                                                    ))})

                                       )
                         (ui-menu-item
                          {}
                          (ui-accordion-title
                           {:active (= active-index 2)
                            :content "Project Lead"
                            :index 2
                            :onClick handleClick
                            :style {:color "#3281b9"}})
                          (ui-accordion-content
                           {:active (= active-index 2)
                            :content (ui-form
                                      {}
                                      (ui-form-group
                                       {:grouped true}
                                       
                                       (map #(ui-form-field
                                              {}                                                                                                                   
                                              (dom/div :.ui.checkbox
                                                       (dom/input
                                                        {
                                                         :type "Checkbox"
                                        ;:checked  in-progress?
                                                         :style {:padding "10px"}
                                                         :onChange (fn [_ d]
                                                                     (comp/transact! this [(project/toggle-project-lead! {:val %})]))})
                                                       (dom/label {:style {:color "#3281b9" }} %)))

                                            

                                            (reduce (fn [acc p]
                                                      (let [st1 (-> p :project-info/project-lead :resource/name )
                                                            st2 (if (keyword? st1)  "" (str st1))
                                                            r? (> (-> st2 count) 1)]
                                                        (js/console.log "ST" st2)
                                                        (if (and r?
                                                                 (not (contains? acc st2)))
                                                          (conj acc st2)
                                                          acc)))
                                                    #{}
                                                    admin-projects))
                                       
                                       ))})

                          )
                         ))
       (ui-grid-column {:width 13}
                       #_(dom/h3 {:style {:textAlign "center"}} "Projects" )
                       (ui-table {:color :blue :style {:fontSize "85%"} :singleLine true  :celled true :sortable true :fixed true}
                                 (ui-table-header {}
                                                  (ui-table-row {:textAlign :center}
                                                                (ui-table-header-cell {:style {:position "sticky" :top 0 } :sorted (when (= column :project/name) direction)
                                                                                       :onClick #(sort-by-column :project/name) } "Project Name")


                                                                (ui-table-header-cell {:style {:position "sticky" :top 0} :sorted (when (= column :project-info/project-lead) direction)
                                                                                       :onClick #(sort-by-column :project-info/project-lead) } "Project Lead")
                                                                (ui-table-header-cell {:style {:position "sticky" :top 0} } "Overall")
                                                                (ui-table-header-cell {:style {:position "sticky" :top 0} } "Client Relationship")
                                                                (ui-table-header-cell {:style {:position "sticky" :top 0} } "Finance")
                                                                (ui-table-header-cell {:style {:position "sticky" :top 0} } "Scope & Schedule")))
                                 (ui-table-body {}
                                                (mapv (fn [p] (ui-table-row {:onClick (fn [e]
                                                                                        
                                                                                        (dr/change-route this (dr/path-to ProjectPanel ProjectInfo {:project-info/id (:project/id p) :project-panel/current-project-id (:project/id p)}))
                                                                                        (comp/transact! this [(project/set-info-as-active-menu)])
                                                                                        (m/set-value! this :ui/active-item :info)
                                        ; (dr/change-route this (dr/path-to ProjectPanel ProjectInfo {:project-info/id (:project/id p) :project-panel/current-project-id (:project/id p)}))
                                                                                        
                                                                                        
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
                                                                                         identity)
                                                                                       (if (and project-leads (not (empty? project-leads)))
                                                                                         (filter #(contains? project-leads (-> %
                                                                                                                               :project-info/project-lead
                                                                                                                               :resource/name ))
                                                                                                 admin-projects)
                                                                                         admin-projects))))
                                 )
                       #_(ui-button {:onClick (fn [] (comp/transact! this [(testm)])) :basic true} "No Name"))]
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))













