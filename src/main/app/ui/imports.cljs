(ns app.ui.imports
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]
   [app.ui.users :as users]
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

(defsc Import [this {:import/keys [type time start-period end-period files fileUrl]}]
  {:query         [:import/id :import/type :import/time #_:import/status :import/end-period
                   :import/fileUrl
                   :import/start-period :import/end-period :import/files]
   :ident         :import/id
   :initial-state {:import/id 1 :import/type :fluxod-timesheet :import/start-period (t/inst (t/now)) :import/end-period (t/inst (t/now))}}
  (let [type->str {:fluxod-timesheet "Fluxod Timesheet"}
        format-time #(apply str (take 16 (str (t/in % (t/zone)))))]
    (ui-table-row
     {}
     (ui-table-cell {} (type->str type))
     (ui-table-cell {:singleLine true} (some-> time format-time))
     (ui-table-cell {:singleLine true} (some-> start-period format-time))
     (ui-table-cell {:singleLine true} (some-> end-period format-time))
     (ui-popup {:basic true
                :trigger (ui-table-cell {:style {:cursor "pointer"}
                                         :onClick #(comp/transact! this
                                                                   [(import/get-import-file {:filename (some-> files first :file/name)})] )}

                                        (dom/a {:href fileUrl :download (some-> files first :file/name) } (some-> files first :file/name)))}
               (ui-popup-content
                {:style {:fontSize "80%"}}
                "Click to download"))

     
     #_(ui-table-cell {} (str status)))))

(def ui-import (comp/factory Import {:keyfn :import/id}))

(defsc ImportMain [this {:ui/keys
                         [modal-open? progress] :imports/keys [new-import imports] :as props}]
  {:query         [:ui/modal-open? {:imports/new-import (comp/get-query Import)} {:imports/imports (comp/get-query Import)}
                   :ui/progress]
   :ident         (fn [] [:component/id :imports])
   :route-segment ["imports"]
   :will-enter (fn [app {:keys [] :as params}]
                 (dr/route-deferred
                  [:component/id :imports]
                  (fn []
                    (df/load!
                     app
                     :all-imports
                     Import
                     {:target [:component/id :imports :imports/imports]
                      :marker :import-main :post-mutation `dr/target-ready :post-mutation-params {:target [:component/id :imports]}}))))
   
   :initial-state {:ui/modal-open? false :imports/new-import {} :imports/imports []}}

  (log/info "VAL OF" (:import/start-period new-import))
  (js/console.log "IMPORTS" imports)
  (ui-container {}
                #_(dom/span {} (str "Progress: " progress))

                (ui-table
                 {:style
                  {:fontSize   "85%"
                   :compact    true
                   :selectable true}
                  :celled true
                  :striped true
                  :color :blue}
                 (ui-table-header
                  {:fullWidth true :style {:position "sticky" :top 0}}
                  (ui-table-row
                   {}
                   (mapv #(ui-table-header-cell {:style {:position "sticky" :top 0}} %) ["Type" "Time" "Start Period" "End Period" "File"])))
                 (ui-table-body
                  {}
                  (mapv ui-import (reverse (sort-by :import/time t/< imports))))
                 (ui-table-footer
                  {}
                  (ui-table-row
                   {:textAlign :right}
                   (ui-table-header-cell
                    {:colSpan 6}
                    (ui-modal
                     {:trigger
                      (ui-icon
                       {:basic true
                        :onClick
                        (fn [e]
                          (m/toggle! this :ui/modal-open?)
                          (merge/merge-component! SPA Import {:import/id (random-uuid)
                                                              :import/type :fluxod-timesheet
                                                              :import/time (js/Date.)
                                                              :import/start-period (t/now)
                                                              :import/end-period (t/now)
                                                              }
                                                  :replace [:component/id :imports :imports/new-import]))
                        
                        :name "plus" }
                       )
                      :open    modal-open?
                      #_#_:onClose #(m/set-value! this :ui/modal-open? false)}
                     (ui-modal-content
                      {}
                      (ui-form
                       {}
                       (ui-form-group
                        {}
                        (ui-form-field
                         {:inline true
                          :content [(dom/label {} "Type")
                                    (ui-dropdown
                                     {:placeholder "Type"
                                      :selection   true
                                      :search      true
                                      :options
                                      [{:text "Fluxod timesheet" :value :fluxod-timesheet}
                                       {:text "MS project" :value :ms-project}]
                                      :value       (:import/type new-import)
                                      :onChange    (fn [e d]
                                                     
                                                     (comp/transact! this [(import/set-new-import-type {:val (keyword (comp/isoget d :value))})]))})]}
                         
                         ))
                       (when (= (:import/type new-import) :fluxod-timesheet)
                         [(ui-form-group
                          {}
                          (ui-form-field
                           {:inline true}
                           (dom/label
                            {} "File")
                           (ui-input
                            {:type     "file"
                             :style
                             {:margin "0px"}
                                        ;:accept   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                             ;; Set `:multiple true` if you want to upload more than one at a time
                             :onChange
                             (fn [evt]
                               (let [files
                                     (file-upload/evt->uploads evt)]
                                 
                                 #_(js/console.log {:upload (-> files first :file/content meta)})
                                 
                                 (comp/transact!
                                  this
                                  [(import/set-import-files {:files files})
                                   (import/produce-dates-from-file
                                    (-> files first (select-keys [:file/name]) ))]
                                  {:optimistic? true})
                                 ))
                                        ;:icon (ui-icon {:name "file outline"} )
                             })))
                         (ui-form-group
                          {}
                          (ui-form-field
                           {:inline true}
                           (dom/label {} "Start Period")
                           (ui-input
                            {:type     "date"
                             :value
                             (import/period-to-string (:import/start-period new-import))
                             :onChange (fn [e e1]
                                         (let [v (js/Date. (evt/target-value e))]
                                           (comp/transact! this [(import/set-start-period {:import/start-period v})])))})))
                         (ui-form-group
                          {}
                          (ui-form-field
                           {:inline true}
                           (dom/label {} "Start Period")
                           (ui-input
                            {:type     "date"
                             :value
                             (import/period-to-string (:import/end-period new-import))
                             :onChange (fn [e e1]
                                         (let [v (js/Date. (evt/target-value e))]
                                           (comp/transact! this [(import/set-end-period {:import/end-period v})])))})))])



                       

                       ))
                     (ui-modal-actions
                      {}
                      [(ui-button
                        {:basic true :onClick (fn []
                                                (m/toggle! this :ui/modal-open?))}
                        "Cancel")

                       (ui-button
                        {:basic true :onClick (fn [e]
                                                (let
                                                    [files (:import/files new-import)]
                                                  
                                                  (cond (= (:import/type new-import) :fluxod-timesheet)
                                                        (comp/transact!
                                                         this
                                                         [(import/import-file
                                                           (file-upload/attach-uploads {:new-import (assoc new-import :import/time (t/inst (t/now)))} files))])

                                                        (= (:import/type new-import) :ms-project)
                                                        (comp/transact!
                                                         this
                                                         [(import/update-db
                                                           {})]))
                                                  (m/toggle! this :ui/modal-open?)
                                                  
                                                  ))}
                        "Run")]))))))))





