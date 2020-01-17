
(ns app.ui.root
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
   [app.ui.users :as users]
   [app.ui.teams :as teams :refer []]



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

   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   

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






(def ui-sticky-table (interop/react-factory StickyTable/StickyTable))
(def ui-sticky-row (interop/react-factory StickyTable/Row))
(def ui-sticky-cell (interop/react-factory StickyTable/Cell))

(def ui-dropdown2 (interop/react-factory Dropdown))
(def dates
  [
   "2019-11-28"
   "2019-11-29"
   "2019-11-30"
   "2019-12-01"
   "2019-12-02"
   "2019-12-03"
   "2019-12-04"
   "2019-12-05"
   "2019-12-06"
   "2019-12-07"
   "2019-12-08"
   "2019-12-09"
   "2019-12-10"
   "2019-12-11"
   "2019-12-12"
   "2019-12-13"
   "2019-12-14"
   "2019-12-15"])



(defn get-from-source [date maps]
  (let [r (first (filter (fn [map] (get map date)) maps))]

    (if (nil? r) {date 0 :assignment/name (:assignment/name (first maps))} r)))

(def dates2 (map #(js/Date. %) dates))






(defn long->year-month-day [x]
  (tf/unparse (tf/formatters :year-month-day) (tc/from-long x)))



(defn field [{:keys [label valid? error-message] :as props}]
  (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
    (div :.ui.field
         (dom/label {:htmlFor label} label)
         (dom/input input-props)
         (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
                  error-message))))

(defmutation configure
  [{:keys [id]}]
  (action [{:keys [state]}]
          (swap! state
                 (fn [s] (-> s
                                        ;(assoc :root/person [:person/id person-id])
                                        ;(fs/add-form-config* PersonForm [:person/id person-id]) ; will not re-add config to entities that were present
                             (fs/mark-complete* [:work-line/id id])
                             (fs/pristine->entity* [:work-line/id id]) ; in case we're re-loading it, make sure the pristine copy it up-to-date
                             ;; it just came from server, so all fields should be valid


                                        ; (fs/pristine->entity*)
                             )))))

(defsc Assignment [this {:assignment/keys [name id day work] :as props}]
  {:query [:assignment/id :assignment/name :assignment/day :assignment/work]
   :ident :assignment/id})



#_(defsc Project [this {:project/keys [id name] :as props}]
    {:query [#_:project/id :project/name  fs/form-config-join
             ]
     :ident :project/name
     :form-fields #{:project/id :project/name} ;
     })

#_(defsc Project [this {:project/keys [id name] :as props}]
    {:query [:project/id :project/name  fs/form-config-join
             ]
     :ident :project/id
     :form-fields #{#_:project/id :project/name}
     })
#_(defsc Assignment [this {:assignment/keys [id name] :as props}]
    {:query [#_:assignment/id :assignment/name fs/form-config-join
             ]
     :ident :assignment/name
     :form-fields #{#_:assignment/id :assignment/name}
     })

(defn table-cell-field [this field {:keys [onChange validation-message input-tag value-xform type]}]
  (let [props         (comp/props this)
        value         (get props field "")
        input-factory (or input-tag dom/input)
        xform         (or value-xform identity)]
    (td
     (input-factory (cond-> {:value    (xform value)
                             :onChange (fn [evt] (when onChange
                                                   (onChange evt)))}
                      type (assoc :type type)))
     (div :.ui.left.pointing.red.basic.label
          {:classes [(when (not= :invalid (work-line/work-line-validator props
                                                                         field)) "hidden")]}
          (or validation-message "Invalid value")))))

(def ui-number-format (interop/react-factory NumberFormat))


                                        ;(def ui-flag (interop/react-factory Flag))
(def ui-flag-icon (interop/react-factory FlagIcon))
(def ui-react-country-flag   (interop/react-factory ReactCountryFlag))
                                        ;(def ui-collapsing-table (interop/react-factory ReactCollapsingTable))
(def ui-timeline (interop/react-factory ReactTimeLine))


(defn ui-money-input
  "Render a money input component. Props can contain:

  :value - The current controlled value (as a bigdecimal)
  :onChange - A (fn [bigdec]) that is called on changes"
  [{:keys [value
           onBlur
           onChange]}]
  (let [attrs {:thousandSeparator true
               :prefix            "$"
               :value             (math/bigdec->str value)
               :onBlur            (fn [] (when onBlur (onBlur)))
               :onValueChange     (fn [v]
                                    (let [str-value (.-value v)]
                                      (when (and (seq str-value) onChange)
                                        (onChange (math/bigdecimal str-value)))))}]
    (ui-number-format attrs)))

(defmutation reset-form [{:keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]

                         (let [new-val-field (get-in s [:work-line/id id ::fs/config ::fs/pristine-state])]

                           (-> s
                               (update-in
                                [:work-line/id id]
                                merge
                                new-val-field)

                                        ; stop editing
                                        ;(dissoc :root/phone)
                                        ; revert to the pristine state
                               (fs/pristine->entity* [:work-line/id id]))))))
  (refresh [env] [:work-day/all-work-lines]))

(defmutation populate-tasks-options [_]
  (action [{:keys [state]}]
          (let [tasks (vals (get @state :task/id))]


            (swap! state assoc :task/options (into []
                                                   (map #(set/rename-keys % {:task/id   :value
                                                                             :task/name :text}))

                                                   tasks)))))



;; components
;; Assignment
;; Project
;; Task
;; AssignmentLine


(defsc WorkLine [this {:ui/keys   [new? saving? saved?]
                       :work-line/keys [project assignment hour]
                       :as        props}]
  {:query       [:work-line/id
                 :ui/new?
                 :ui/saving?
                 :ui/saved?
                 :work-line/hour
                 :work-line/project
                 :work-line/assignment
                 fs/form-config-join]
   :form-fields #{:work-line/hour  :work-line/project :work-line/assignment
                  }
   :pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config WorkLine data-tree))

   :componentDidUpdate (fn [this prev-props prev-state])

   :componentDidMount         (fn [this]
                                (let [props (comp/props this)
                                      project-id (:project/id (:work-line/project props))
                                      work-line-id (:work-line/id props)]

                                  #_(df/load this :work-line/assignments Assignment
                                             {:params               {:project-id project-id}
                                              :post-mutation        `populate-tasks-options

                                              })
                                        ;(comp/transact! this [(fs/mark-complete! [:work/line work-line-id])])
                                  ))
   :ident       :work-line/id

   }
  (let [project-options (get props :project/options)
        task-options (get props :task/options)]
    (tr
     (td
      project
      )
     (td assignment)

     (td hour)


     #_(table-cell-field this :work-line/hour {}))))

(def ui-work-line (comp/factory WorkLine {:keyfn (fn [props] (:work-line/id props))}))

(defsc WorkDay [this {:work-day/keys [all-work-lines] :as props}]
  {:query         [{:work-day/all-work-lines (comp/get-query WorkLine)}]
   :initial-state {:work-day/all-work-lines []}
   :ident         (fn [] [:component/id :work-day])
   :route-segment ["work-day"]}


  (let [total (reduce
               (fn [amt {:work-line/keys [hour]}]
                 (+ amt hour))
               0
               all-work-lines)]

    (table :.ui.table
           (thead (tr (th "Project" ) (th "Assignment") (th "# Hours")))
           (tbody (map ui-work-line all-work-lines))
           (tfoot (tr (th "        ") (th "      ")  (th "Total: " total))))))

(def ui-work-day (comp/factory WorkDay {:keyfn :work-day/work-lines}))


(def ui-date-input (interop/react-factory SemanticUICalendar/DateInput))

(def sample-server-response
  [{:work-line/id 13,
    :work-line/project {:project/id 2, :project/name "Hello"},
    :work-line/hours 22}
   {:work-line/id 14,
    :work-line/project {:project/id 2, :project/name "Hello"},
    :work-line/hours 8}])

(def component-query [{:work-day/all-work-lines (comp/get-query WorkLine)}])



#_(defmutation load-all-work-lines [_]
    (action [{:keys [state ref app]}]
            (-> state
                (swap! merge/merge-component WorkLine {:work-line/id 13

                                                       :work-line/project
                                                       {:project/id 2 :project/name "Hello"}

                                                       :work-line/hours 3}

                       #_{:work-line/id 1

                          :work-line/project
                          {:project/id 2 :project/name "Hello"}

                          :work-line/hours 3}


                       :append [:component/id :work-day :work-day/all-work-lines]))




            (dr/change-route app ["work-day"])))


#_(def date-stored (atom nil))






(defn format-date [d]
  (let [[day month year] (clojure.string/split d #"-")]
    (js/Date. (str year "-" month "-" day))))


(defsc Date [this {:date/keys [selected-day ] :as props}]
  {:query         [:date/selected-day
                   '[:component/id :session]]
   :initial-state (fn [_] {:date/selected-day  (tf/unparse (tf/formatters :year-day-month) (tt/now))})
   :ident         (fn [] [:component/id :date])
                                        ;:route-segment ["main"]
   }

  (comp/fragment
   (ui-grid-row {:stretch true}
                (dom/div :.ui.container

                         (ui-date-input  {:inline true :name "Date"
                                          :value (log/spy :info selected-day)
                                          :marked  ["15-11-2019" "16-11-2019" "14-11-2019"]
                                          :markColor "grey"
                                          
                                          
                                        ;  :componentDidMount

                                          ;; (fn [this props state]
                                          ;;   (df/load! this :work-day/all-work-lines
                                          ;;             WorkLine
                                          ;;             {:target [:component/id :work-day :work-day/all-work-lines]
                                          ;;              :params {:work-day 2}
                                          ;;              }
                                          ;;             ))
                                          :onChange (fn [x y]
                                                      (let [current-date (:value (clojure.walk/keywordize-keys (js->clj y)))

                                                            custom-formatter (tf/formatter "dd-mm-yyyy")



                                                            date-instant
                                                            (js/Date. (tf/unparse (tf/formatters :date-hour-minute-second)(tf/parse custom-formatter current-date)))

                                                            {username :account/name} (get props [:component/id :session])

                                                            ]

                                                        (df/load! this  :work-day/all-work-lines
                                                                  WorkLine
                                                                  {:target [:component/id :work-day :work-day/all-work-lines]
                                                                   :params {:by-day (format-date current-date) :username username}
                                                                   }

                                        ;:post-mutation work-line/add-form-config
                                                                  ))

                                                      (dr/change-route this ["work-day"])
                                                      ;; TODO using parameters?
                                                      ;;TODO  we load all data for that  instance of time


                                        ;                               (comp/transact! this [(load-all-work-lines)])
                                                      )
                                        ;(fn [evnt data] (js/console.log data))
                                          })))
   )


                                        ;(ui-calendar {})
  )

(def ui-date (comp/factory Date))


(defsc Main [this {:main/keys [date welcome-message]}]
  {:query         [:main/welcome-message {:main/date (comp/get-query Date)}]
   :initial-state (fn [_] {:main/welcome-message "Hi!"
                           :main/date (comp/get-initial-state Date {})})
   :ident         (fn [] [:component/id :main])
   :route-segment ["main"]}





  #_(ui-date date))

(defsc SignupSuccess [this props]
  {:query         ['*]
   :initial-state {}
   :ident         (fn [] [:component/id :signup-success])
   :route-segment ["signup-success"]}
  (div
   (dom/h3 "Signup Complete!")
   (dom/p "You can now log in!")))

(defsc Signup [this {:account/keys [email password password-again] :as props}]
  {:query             [:account/email :account/password :account/password-again fs/form-config-join]
   :initial-state     (fn [_]
                        (fs/add-form-config Signup
                                            {:account/email          ""
                                             :account/password       ""
                                             :account/password-again ""}))
   :form-fields       #{:account/email :account/password :account/password-again}
   :ident             (fn [] session/signup-ident)
   :route-segment     ["signup"]
   :componentDidMount (fn [this]
                        (comp/transact! this [(session/clear-signup-form)]))}
  (let [submit!  (fn [evt]
                   (when (or (identical? true evt) (evt/enter-key? evt))
                     (comp/transact! this [(session/signup! {:email email :password password})])
                     (log/info "Sign up")))
        checked? (fs/checked? props)]
    (div
     (dom/h3 "Signup")
     (div :.ui.form {:classes [(when checked? "error")]}
          (field {:label         "Email"
                  :value         (or email "")
                  :valid?        (session/valid-email? email)
                  :error-message "Must be an email address"
                  :autoComplete  "off"
                  :onKeyDown     submit!
                  :onChange      #(m/set-string! this :account/email :event %)})
          (field {:label         "Password"
                  :type          "password"
                  :value         (or password "")
                  :valid?        (session/valid-password? password)
                  :error-message "Password must be at least 8 characters."
                  :onKeyDown     submit!
                  :autoComplete  "off"
                  :onChange      #(m/set-string! this :account/password :event %)})
          (field {:label         "Repeat Password" :type "password" :value (or password-again "")
                  :autoComplete  "off"
                  :valid?        (= password password-again)
                  :error-message "Passwords do not match."
                  :onChange      #(m/set-string! this :account/password-again :event %)})
          (dom/button :.ui.primary.button {:onClick #(submit! true)}
                      "Sign Up")))))

(declare Session)

(defsc Login [this {:account/keys [email]
                    :ui/keys      [error open?] :as props}]
  {:query         [:ui/open? :ui/error :account/email
                   {[:component/id :session] (comp/get-query Session)}
                   [::uism/asm-id ::session/session]]
   :css           [[:.floating-menu {:position "absolute !important"
                                     :z-index  1000
                                     :width    "300px"
                                     :right    "0px"
                                     :top      "50px"}]]
   :initial-state {:account/email "" :ui/error ""}
   :ident         (fn [] [:component/id :login])}
  (let [current-state (uism/get-active-state this ::session/session)
        {current-user :account/name} (get props [:component/id :session])
        initial?      (= :initial current-state)
        loading?      (= :state/checking-session current-state)
        logged-in?    (= :state/logged-in current-state)
        {:keys [floating-menu]} (css/get-classnames Login)
        password      (or (comp/get-state this :password) "")] ; c.l. state for security



    (when-not initial?
      (if logged-in?
        (dom/button :.item
                    {:onClick #(uism/trigger! this ::session/session :event/logout)}
                    (dom/span current-user) ent/nbsp "Log out")

        (dom/div {:style {}
                  :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
                 "Login"
                 (when open?
                   (dom/div :.four.wide.ui.raised.blue.segment {:onClick (fn [e]
                                                                           ;; Stop bubbling (would trigger the menu toggle)
                                                                           (evt/stop-propagation! e))
                                                                :classes [floating-menu]}
                            (dom/h3 :.ui.header "Login")
                            (div :.ui.form {:classes [(when (seq error) "error")]}
                                 (field {:label    "Email"
                                         :value    email
                                         :onChange #(m/set-string! this :account/email :event %)})
                                 (field {:label    "Password"
                                         :type     "password"
                                         :value    password
                                         :onChange #(comp/set-state! this {:password (evt/target-value %)})})
                                 (div :.ui.error.message error)
                                 (div :.ui.field
                                      (dom/button :.ui.button
                                                  {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
                                                                                                                       :password password}))
                                                   :classes [(when loading? "loading")]} "Login"))





                                 #_(div :.ui.message
                                        (dom/p "Don't have an account?")
                                        (dom/a {:onClick (fn []
                                                           (uism/trigger! this ::session/session :event/toggle-modal {})
                                                           (dr/change-route this ["signup"]))}
                                               ))))))))))

(def ui-login (comp/factory Login))




#_(defsc Main [this props]
    {:query         [:main/welcome-message]
     :initial-state {:main/welcome-message "Hi!"}
     :ident         (fn [] [:component/id :main])
     :route-segment ["main"]}
    (div :.ui.container.segment
         (h3 "Main")))

(defsc Main [this {:main/keys [date welcome-message]}]
  {:query         [:main/welcome-message {:main/date (comp/get-query Date)}]
   :initial-state (fn [_] {:main/welcome-message "Hi!"
                           :main/date (comp/get-initial-state Date {})})
   :ident         (fn [] [:component/id :main])
   :route-segment ["main"]}


  (div (ui-date date)
       #_(div  {:style {:flex 1 :display "flex"
                        :marginTop "200px"
                        :alignItems "center"
                        :color "gray"
                        :textAlign "center"
                        :justifyContent "center"}}  "made with  " (ui-icon {:style {:position "relative" :bottom "3px" :left "2px"} :name "true like"  }) " in MTL"

                                        ;(ui-flag-icon #js {:code "CA"})

                        ;; (Flag {:code "CA"})

                        ;; (ui-react-country-flag {:code "US" :svg true :title "CA" :style {:width "2em" :height "2em"}})
                        ))

  )





(defsc Project
  [this {:project/keys [id name]}]
  {:query [:project/id :project/name :project/last-published-date :project/modified-date]
   :ident :project/id})

(defsc SelectedProject
  [this {:selected/keys [project]}]
  {:query [:selected/project (comp/get-query Project)
           {:selected/resource (comp/get-query users/Resource)}]
   :ident (fn [] [:component/id :selected])}


  (dom/p {} "sd"))

(def ui-selected-project (comp/factory SelectedProject))

(def ui-selected-comp (comp/factory SelectedProject))

(defn ui-selected [{:selected/keys [project resource]}]

  (dom/p (:project/name project)))









(defsc AssignmentTable [this {:keys [assignments]}]
  {:query [{:assignments (comp/get-query Assignment)}]
   :indent [:component/id :assignment]}

  (dom/p {} (str (:assignment/name (first assignments)))))



(def ui-assignment-table (comp/factory AssignmentTable))


#_(defn set-selected
    [this payload]
    (let []
      (comp/set-state! this payload)
      (df/load! this :assignments Assignment
                { ;:post-mutation        `project/populate-projects
                 :append [:component/id :resources :resources/assignments]})))


(defn generate-row-dates [start end]
  (loop [s (tf/parse (tf/formatters :date) start)
         r []]
    (if (not= end (tf/unparse (tf/formatters :date) s))
      (recur (tt/plus s (tt/days 1)) (conj r (tf/unparse (tf/formatters :date) s)))
      r)))


(defn generate-row-dates-readable
  [start end]
  (loop [s (t/date start)
         r []]
    (if (= (str end) (str s))
      r
      (let [day-of-week (apply str (take 3 (str (t/day-of-week s))))
            day-of-month (t/day-of-month s)
            month (apply str (take 3 (str (t/month s))))
            year (t/year s)]
        (recur (t/+ s (t/new-period 1 :days))
               (conj r (str (str/capitalize day-of-week) ". " (str (str/capitalize month) ". " day-of-month) " " year)))))))





(defsc ProjectLine [this {:keys [project-line/id
                                 project-line/resource
                                 project-line/assignments
                                 project-line/project
                                 ui/selected
                                 ui/dates] :as props}]
  {:query [:project-line/id
           [:ui/dates '_]
           {:project-line/project (comp/get-query Project)}
           {:project-line/resource (comp/get-query users/Resource)}
           {:project-line/assignments (comp/get-query Assignment)}
           :ui/selected ]
   :ident (fn [] [:project-line/id id])
   :initial-state {:ui/selected false}



   }


  (let [state-map (comp/component->state-map this)
        start (:start dates)
        end (:end dates)

        color (fn [n] (cond
                        (= n 0) "white"
                        (and (> n 0) (<= n 8)) "lightGreen"
                        (and (> n 8) (<= n 10)) "orange"
                        (> n 10) "red"))


        r-step
        (map
         (fn [[k vals]]
           (reduce (fn [r x] (let [day (:assignment/day x)
                                   work (:assignment/work x)
                                   m {day work :assignment/name k}]
                               (conj r m)

                               )) [] vals)) (group-by :assignment/name assignments))


        transformed (map
                     (fn [r]
                       (map (fn [date] (get-from-source date r) ) (map #(js/Date. %)
                                                                       (generate-row-dates start end))))
                     r-step



                     )

        total (reduce (fn [r row] (+ r (reduce #(+ %1 (first (vals %2))) 0 row)) ) 0 transformed)]


    ;; TODO when totoal is 0 don't show it
    
    (when (> total 0 )
      (comp/fragment

       
       (ui-table-row
        {:style {}
         :onClick (fn []
                    #_(comp/set-state! this {:ui/selected (not (:ui/selected (comp/get-state this)))})
                    (m/toggle! this :ui/selected)
                    )}

        (ui-table-cell  {:style {:backgroundColor  "#3281b9"}} "") (ui-table-cell 

                                                                    {:colSpan 2 :title (apply str (take 21 (str (:project/last-published-date project))))}
                                                           (str (:project/name project) " ")
                                                           (when (not selected) (ui-icon {:name "angle down" :link true :style {:display "inline" :z-index -1}})))

        

        (map #(td {:style {:backgroundColor (color %)}}
                  (goog.string.format "%.2f" %))
             (loop [r transformed
                    t []]
               (if (ffirst r)
                 (let [total (reduce (fn [r m] (+ r (first (vals m)))) 0 (map first r))]
                   (recur (map rest r) (conj t total)))
                 t))))

       (when selected

         (comp/fragment
          (map (fn [asses]
                 ;; hide assignment line with 0 work load

                 (when (> (reduce (fn [r m] (+ r (first (vals m)))) 0 asses) 0)
                   (tr (concat [(td  {:colSpan 2 :style {:backgroundColor  "#3281b9"}} "")
                                (ui-table-cell {:singleLine true}
                                               (:assignment/name (first asses)))]
                               (map #(td {:style {:background-color (color (first (vals %)))}}
                                         (goog.string.format "%.2f" (first (vals %)))) asses))))
                 )
               transformed)))))))

(def ui-project-line (comp/factory ProjectLine {:keyfn :project-line/id}))





(defsc ResourceLine [this {:resource-line/keys [resource project-lines totals]
                           :keys [ui/selected ui/dates]
                           :as props}]
  {:query
   [:ui/selected
    {:resource-line/resource (comp/get-query users/Resource)
     }
    :resource-line/totals
    [:ui/dates '_]
    :resource-line/id
    [df/marker-table :resource-line-loader]
    {:resource-line/projects (comp/get-query Project)}
    {:resource-line/project-lines (comp/get-query ProjectLine)}
    ]
   :initLocalState (fn [this props] {:count 0})
   :ident :resource-line/id
   :shouldComponentUpdate (fn [_ _ _] true)
   
   #_#_:getDerivedStateFromProps
   (fn [props state]
     (when (= (:count state) 0)
       
       ())
     (let [ 
           id (:resource-line/id
               props)
           project-lines-r

           (:resource-line/project-lines props)
           

           ;; TODO resolve projects 
           start (:start (:ui/dates props))
           end (:end (:ui/dates props))
           
           transform-project-line

           (fn [pl]
             (let [asses (:project-line/assignments pl)
                   asses-resolved]
               
               (map
                (fn [r]
                  
                  (map (fn [date] (get-from-source date r) ) (map #(js/Date. %)
                                                                  (generate-row-dates start end))))
                
                (map
                 (fn [[k vals]]
                   (reduce (fn [r x] (let [day (:assignment/day x)
                                           work (:assignment/work x)
                                           m {day work :assignment/name k}]
                                       (conj r m)
                                       
                                       )) [] vals))
                 (group-by :assignment/name
                           (map (fn [ident] (get-in state ident))
                                )))

                )


               )
             )
           
           ]
       )
     nil
     )


   
   #_#_:componentDidUpdate (fn [this next-props next-state]
                             )



   
   

   :componentDidMount (fn [this]
                        (let
                            [start (:start (:dates (comp/props this)))
                             
                             end (:end (:dates (comp/props this)))
                             
                             resource-line-id (:resource-line/id (comp/props this))
                             {:resource-line/keys [resource projects]}
                             (:resource-line/resource (comp/props this))

                             ]

                         (df/load! this :projects Project
                                    {:marker :resource-line-loader
                                     :params               {:resource/id resource-line-id}

                                     :target [:resource-line/id  resource-line-id :resource-line/projects]
                                        ;:post-mutation        `project/populate-projects
                                     :post-mutation `set-projects-lines
                                     ;; we get the resource from workplan
                                     :post-mutation-params {:projects (:resource-line/projects (comp/props this)) :resource-id (:resource-line/id (comp/props this))}
                                     
                                     })




                          (let [project-lines (get-in  [:resource-line/id  resource-line-id]

                                                       (comp/component->state-map this))]


                            )




                          )
                        )
   


   }

  ;; TODO extract common functions to top




  (let [color (fn [n] (cond
                        (= n 0) "white"
                        (and (> n 0) (<= n 8)) "lightGreen"
                        (and (> n 8) (<= n 10)) "orange"
                        (> n 10) "red"))
        marker (get (comp/props this) [df/marker-table :resource-line-loader])]

    (if (not (df/loading? marker))
      (comp/fragment
      (tr
       {:style {}
        :onClick (fn []
                   (m/toggle! this :ui/selected)
                   )}

       (td {:style {} :colspan 3}
           
           (str (:resource/name resource) " ")
           (when (not selected) (ui-icon {:name "angle down" :link true :style {:display "inline"}})))

       (when totals (map #(td {:style {:backgroundColor (color %)}}

                              
                              (goog.string.format "%.2f" %)) totals))


       )

      (when selected
        (map ui-project-line project-lines)))
      (tr (td {:colSpan 8}

              (ui-loader {:active true :inline :centered} ))))))
(def ui-resource-line (comp/factory ResourceLine))



(defmutation set-project-line [{:keys [resource project  id]}]
  (action [{:keys [state] :as env}]


          (when (swap! state assoc-in [:project-line/id id :project-line/resource] resource)
           (swap! state assoc-in [:project-line/id id :project-line/project] project)
           (swap! state assoc-in [:project-line/id id :project-line/id] id)

           (swap! state (df/load   :assignments Assignment
                                   {:params               {:resource/id (:resource/id resource)
                                                           :project/id (:project/id project)}
                                    :target [:project-line/id id :project-line/assignments]
                                    
                                    }))) ))

(defmutation set-totals [{:keys [resource-line-id] :as params}]
  (action [{:keys [state] :as env}]

          (let [project-lines (get-in @state [:resource-line/id resource-line-id :resource-line/project-lines])
                project-lines-r (map (fn [ident] (get-in @state ident)) project-lines)
                start (get-in @state [:ui/dates :start])
                end (get-in @state [:ui/dates :end])
                ;; TODO abstract duplication see project line
                transform-pl 
                (fn [pl start end]


                  (let [asses (map (fn [ident] (get-in @state ident))
                                   (:project-line/assignments pl))]
                    (js/console.log "asses" (:project-line/assignments pl))
                    (map
                     (fn [r]
                       (map (fn [date] (get-from-source date r) ) (map #(js/Date. %)
                                                                       (generate-row-dates start end))))
                     
                     (map
                      (fn [[k vals]]
                        (reduce (fn [r x] (let [day (:assignment/day x)
                                                work (:assignment/work x)
                                                m {day work :assignment/name k}]
                                            (conj r m)

                                            )) [] vals)) (group-by :assignment/name asses))))
                  )

                totals
                (apply map +
                       (map (fn [line]
                              (map #(first (vals %)) line))
                            (mapcat #(transform-pl % start end) project-lines-r)))]

            
            (swap! state assoc-in [:resource-line/id resource-line-id :resource-line/totals] totals))))


(defmutation set-projects-lines [{:keys [projects resource-id]}]
  (action [{:keys [state] :as env}]

          (when resource-id
            (let
               [projects-s (get-in @state [:resource-line/id resource-id  :resource-line/projects])
                resource-s (get-in @state [:resource-line/id resource-id  :resource-line/resource])

                projects-resolved (map (fn [[_ project-id]]
                                         (get-in @state [:project/id project-id]))
                                       projects-s)
                resource-resolved (get-in @state [:resource/id resource-id])
                project-lines-ids (atom [])]



             (doseq [c-project (butlast projects-resolved)]



               (let [project-line-id (random-uuid)
                                        ;state-map (comp/component->state-map this)
                     ]

                                        ;(swap! state project-lines-ids conj [:project-line/id project-line-id])




                 #_(df/load! SPA :assignments Assignment
                             {:params               {:resource/id (:resource/id resource-resolved)
                                                     :project/id (:project/id c-project)}
                              :target (:project-line/id project-line-id :project-line/assignments)})


                 (swap! state (fn[state]

                                (-> state

                                    (merge/merge-component ProjectLine {:project-line/resource resource-resolved
                                                                        :project-line/project c-project
                                                                        :project-line/id project-line-id

                                                                        })

                                    


                                    )


                                )

                        )


                 
                 (df/load! SPA :assignments Assignment
                           {:params               {:resource/id (:resource/id resource-resolved)
                                                   :project/id (:project/id c-project)}
                            :target [:project-line/id project-line-id :project-line/assignments]
                            #_:post-mutation})


                 
                 (swap! project-lines-ids conj project-line-id)

                 
                 )
               
               )


             (let [project-line-id (random-uuid)]
               (swap! state (fn[state]

                              (-> state

                                  (merge/merge-component ProjectLine {:project-line/resource resource-resolved
                                                                      :project-line/project (last projects-resolved)
                                                                      :project-line/id project-line-id

                                                                      })
                                  )

                              )
                      )
               (df/load! SPA :assignments Assignment
                         {:params               {:resource/id (:resource/id resource-resolved)
                                                 :project/id (:project/id (last projects-resolved))}
                          :target [:project-line/id project-line-id :project-line/assignments]
                          :post-mutation `set-totals
                          :post-mutation-params {:resource-line-id (:resource/id resource-resolved)}
                          #_:post-mutation})
               (swap! project-lines-ids conj project-line-id))


             
             (swap! state assoc-in [:resource-line/id  resource-id  :resource-line/project-lines] (mapv (fn [id] [:project-line/id id])@project-lines-ids))
             (swap! state update-in [:component/id :workplan :ui/loading] not)))



          ;; TODO move loading to the level of resource line
          

          
          ))





(defmutation set-project-lines-for-resources [{:keys [project-lines-ids]}]
  (action [{:keys [state] :as env}]


          (swap! state (fn [s]

                         (-> s

                             (assoc-in [:component/id :resources :resources/project-lines] project-lines-ids)
                             )

                         ))
          ))



#_(defn set-project-line [{:keys [resource project ident id state]}]
    (-> state
        (assoc-in [:project-line/id id :project-line/resource]  resource)
        (assoc-in [:project-line/id id :project-line/project]  project)
        (assoc-in [:project-line/id id :project-line/id]  id)
        (update-in [:component/id :resources :resources/project-lines] conj [:project-line/id id])
        (df/load :assignments Assignment
                 {:params               {:resource/id (:resource/id resource)
                                         :project/id (:project/id project)}
                  :target [:project-line/id id :project-line/assignments]

                  })
        ))


(defmutation set-project-line-assignments [{:keys [id ident]}]
  (action [{:keys [state] :as env}]


          (swap! state (fn [s]
                         (-> s
                             (assoc-in (conj ident :project-line/assignments) (:assignments s)))))
          state))


(def asses (atom []))









#_(defsc ProjectLine [this [{:project-line/keys [project resource] :as props}]]
    {:query [:project-line/project :project-line/resource]}
    (tr (td "Hi")))

(defmutation set-resource [{:keys [resource]}]
  (action [{:keys [state] :as env}]

          (swap! state assoc-in [:component/id :projects-table :projects-table/resource]
                 [:resource/id (:resource/id resource)])))

(defsc ProjectsTable [this {:projects-table/keys [resource projects project-lines] :as props}]
  {:query [:projects-table/projects :projects-table/resource :projects-table/project-lines]
   :ident (fn [] [:component/id :projects-table])
                                        ;:componentDidMount (fn [this]
                                        ; )
   }


  )


(def ui-projects-table (comp/factory ProjectsTable))


(defmutation set-projects [{:keys [projects resource]}]
  (action [{:keys [state]}]
          (swap! state merge/merge-component ProjectsTable {:projects-table/projects projects :project-table/resource resource})))

(defmutation set-workplan-date
  [{:keys [start end]}]
  (action [{:keys [state]}]

          (when start
            (swap! state assoc-in [:ui/dates :start] start))
          (when end
            (swap! state assoc-in [:ui/dates :end] end))
          ))


(defmutation set-resource-lines
  [{:keys [ids]}]
  (action [{:keys [state]}]
          
          (doseq [id ids]
            (swap! state merge/merge-component ResourceLine
                   {:resource-line/resource [:resource/id id]
                    :resource-line/id id}
                   ))
          (swap! state assoc-in [:component/id :workplan :workplan/resource-lines
                                 ] (mapv (fn [id] [:resource-line/id id]) ids)))

  )

(defmutation check-all-resource-boxes [{:keys []}]
  (action [{:keys [state] :as env}]
          
          (let [active-checkboxes (filter #(:resource/active? %)
                                          (vals (:checkbox/id @state)))
                active-ids  (map :checkbox/value active-checkboxes)]
            (doseq [id active-ids]
              
              (swap! state assoc-in [:checkbox/id id :ui/checked?] true)
              )
            (js/console.log "active ids" active-ids)
            (comp/transact! SPA  [(set-resource-lines {:ids active-ids})]))
          
          ))

(defmutation remove-resource-line [{:keys [id]}]
  (action [{:keys [state] :as env}]
          
          (swap! state merge/remove-ident* [:resource-line/id id] [:component/id :workplan :workplan/resource-lines])
          (swap! state update-in [:team/id] dissoc  id ))
  )

(defmutation uncheck-all-resource-boxes [{:keys []}]
  (action [{:keys [state] :as env}]
          (let [ids  (map first (:checkbox/id @state))]
            (doseq [id ids]
              (swap! state assoc-in [:checkbox/id id :ui/checked?] false)
              (comp/transact! SPA  [(remove-resource-line {:id id})])
              )
            )
          
          ))



(defn item-checked? [item] (:ui/checked? item))





(defsc ResourceCheckboxItem [this {:keys [resource/name ui/checked?]
                                   :checkbox/keys [label value]
                                   :as props}]

  
  {:query [:checkbox/value :checkbox/label :resource/name :ui/checked?]
   
   :ident (fn [] [:checkbox/id (:checkbox/value props)])
   ;:initLocalState (fn [this] {:ui/checked? false})
   
   :initLocalState (fn [this props]
                     {:checked false})
   }

  
  (let []
    (dom/div :.ui.checkbox
             (dom/input   {
                           :type "checkbox"
                           :checked checked?
                           :indeterminate true
                           :style {:padding "10px"}
                           
                           :onChange (fn [_ d]
                                       ;(comp/set-state! this {:checked (not (comp/get-state this :checked))})
                                       (m/toggle! this :ui/checked?)
                                       
                                       (if (not checked?)
                                         ;(comp/transact! SPA  [(set-resource-lines {:ids [value]})])
                                         (merge/merge-component! SPA  ResourceLine
                                                                 {:resource-line/resource [:resource/id value]
                                                                  :resource-line/id value}
                                                                 :append [:component/id :workplan :workplan/resource-lines])
                                         (comp/transact! this [(remove-resource-line {:id value})])
                                         )
                                       )})
             (dom/label {:style {:color "#3281b9" }} label))))


(def ui-resource-checkbox-item  (comp/factory ResourceCheckboxItem {:keyfn :checkbox/value}))

(defsc ResourcesCheckboxes [this {:list/keys [items all-checked? show-more?]}]
  {:query [{:list/items (comp/get-query ResourceCheckboxItem)  } :list/all-checked? :list/show-more?]
   :ident (fn [] [:component/id :checkboxes])
   ;:shouldComponentUpdate (fn [_ _ _] true)
   :initLocalState (fn [_] {:all-checked? false :list/show-more? false})}
  (let [#_#_all-checked? (every? item-checked? items)
        show-more? (comp/get-state this :list/show-more?)]

    (js/console.log "item2s" items)
    (dom/div :.ui.checkbox
             (dom/input {
                         :type "checkbox"
                         :style {:color "#3281b9"}
                         :checked (comp/get-state this :all-checked?)
                         :onClick (fn [a b]
                                    
                                    (comp/set-state! this {:all-checked? (not (comp/get-state this :all-checked?))} )
                                    (m/toggle! this :all-checked?)
                                    
                                    (let [{:keys [all-checked?]}(comp/get-state this)]
                                      
                                      (if all-checked?
                                        (comp/transact! this [(uncheck-all-resource-boxes )])
                                        (comp/transact! this [(check-all-resource-boxes )])
                                        
                                        
                                        )))})
             (dom/label {:style {:color "#3281b9"}} "Check all")
             (ui-divider {})
             (js/console.log "ITEMS" items)
             (map #(ui-resource-checkbox-item  % )
                  (take (if show-more? 100 10) items))
             (ui-button {:size "mini" :basic true :style {:marginLeft "30px" :marginTop "5px"}
                         :onClick (fn [e]
                                    #_(m/toggle! this :list/show-more?)
                                    (comp/set-state! this  {:list/show-more? (not show-more?)}))}
                        (if show-more? "Show less" "Show more")))))

(def ui-resources-checkboxes  (comp/factory ResourcesCheckboxes))




(defmutation set-resource-line-for-team [{:keys [team-id]}]
  (action [{:keys [state] :as env}]

          (let [team (get-in @state [:team/id team-id])
                resource-lines (get-in @state [:component/id :workplan :workplan/resource-lines])
                new-resource-lines (mapv (fn [[_ id]] [:resource-line/id id]) (:team/resources team))
                combined-resource-lines (vec (distinct (concat resource-lines new-resource-lines)))]
            ;; todo add team lead
            (js/console.log "HIIIII" combined-resource-lines)
            (doseq [[_ id] (cond-> (:team/resources team) (:team/lead team) (conj (:team/lead team)))]
              (swap! state merge/merge-component ResourceLine
                     {:resource-line/resource [:resource/id id]
                      :resource-line/id id}
                     :append [:component/id :workplan :workplan/resource-lines
                              ]
                     ))
            (println "HERE" (cond-> (:team/resources team) (:team/lead team) (conj (:team/lead team))))
            (doseq [[_ id] (cond-> (:team/resources team) (:team/lead team) (conj (:team/lead team)))]
              (swap! state assoc-in [:checkbox/id id :ui/checked?] true))
            ;; (swap! state assoc-in [:component/id :workplan :workplan/resource-lines
            ;;                        ] combined-resource-lines)
            ;; (swap! state (fn [s]

            ;;                (-> s
                               
                               
            ;;                    )

            ;;                ))
            )
          ))




(defmutation remove-resource-line-for-team [{:keys [team-id]}]
  (action [{:keys [state] :as env}]

          (let [team (get-in @state [:team/id team-id])
                team-resources (:team/resources team)
                team-resources-ids (set (map second team-resources))
                team-lead (:team/lead team)

                
                ]
            (doseq [[_ id] (cond-> (:team/resources team) (:team/lead team) (conj (:team/lead team)))]
              (swap! state merge/remove-ident* [:resource-line/id id] [:component/id :workplan :workplan/resource-lines])
              (swap! state assoc-in [:checkbox/id id :ui/checked?] false))
            
            ;; todo add team lead
            
            

            
            )
          ))


(defsc TeamCheckbox [this {:keys [db/id ui/checked?] :as team}]
  {:query (fn []
            [:team/name
             :ui/checked?
            :db/id
            ])
;   :initial-state (fn [p] {:ui/checked? true})
   
   :ident (fn [] [:team-checkbox/id id])}
 ; :shouldComponentUpdate (fn [_ _ _] true)
  (let []
    (dom/div {}
            (dom/div :.ui.checkbox (dom/input  {:style {}
                                                :type "checkbox"
                                                :checked checked?
                                        ;:indeterminate true
                                        ;:style {:padding "10px"}
                                                
                                                :onChange (fn [e]
                                                            

                                                            (m/toggle! this :ui/checked?)

                                                            (js/console.log "checked" checked?)
                                                            
                                                            
                                                            (if  checked?
                                                              (comp/transact! this [(remove-resource-line-for-team {:team-id (:db/id team)})])
                                                              ;; TAG

                                                              ;; (merge/merge-component! SPA  ResourceLine
                                                              ;;                         {:resource-line/resource [:resource/id value]
                                                              ;;                          :resource-line/id value}
                                                              ;;                         :append [:component/id :workplan :workplan/resource-lines])
                                                              (comp/transact! this [(set-resource-line-for-team {:team-id (:db/id team)})])
                                                              
                                                              )
                                                            
                                                            
                                                            )})
                     (dom/label {:style {:color "#3281b9"}} (:team/name team))
                     (dom/br {})))))


(def ui-team-checkbox (comp/factory TeamCheckbox))

(defsc WorkPlan [this {:workplan/keys [resource-lines team-checkboxes]
                       :keys [ui/dates ui/loading ui/show-more? workplan/teams]:as props}]
  {:query         [{:workplan/resource-lines (comp/get-query ResourceLine)}

                   :ui/loading :ui/show-more?
                                        ;{:workplan/projects (comp/get-query Project)}
                                        ;{:workplan/project-lines (comp/get-query ProjectLine)}
                   [::uism/asm-id ::session/session]

                                        ;{:workplan/selected (comp/get-query SelectedProject)}

                   [:resource/options '_]
                   {:workplan/teams (comp/get-query teams/Team)}  
                   [:checkbox/id '_]
                   [:team/id '_]
                   {:workplan/team-checkboxes (comp/get-query TeamCheckbox)}
                   [:ui/dates '_]
                   [df/marker-table :projects]]
   :ident         (fn [] [:component/id :workplan])
   :route-segment ["workplan"]
   :shouldComponentUpdate (fn [_ _ _] true)
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :workplan]
                  (fn []
                    (df/load! app :resource/all-resources users/Resource {:post-mutation `resource/create-resource-options :target

                                                                          [:component/id :admin-users :admin-users/resources]})
                    (df/load! app :teams teams/Team

                              {:target 


                               (targeting/multiple-targets
                                        ;(targeting/append-to [:component/id :admin-teams :teams/teams])
                                (targeting/replace-at [:component/id :workplan :workplan/teams])
                                (targeting/replace-at [:component/id :admin-teams :teams/teams])
                                  )
                               
                               :post-mutation `app.ui.teams/merge-team-checkboxes
                               })
                    (comp/transact! app [(dr/target-ready {:target [:component/id :workplan]})]))))
   
                                        ;:shouldComponentUpdate (fn [_ _ _] true)
   :initial-state (fn [params]
                    {

                     :ui/loading false
                     :ui/show-more? false
                     :ui/all-checked? false

                     ;; :workplan/resource-lines
                     
                     ;; [
                     ;;  {
                     ;;   :resource-line/id #uuid "d771d4d9-34de-e911-b085-00155de0a811"                                               :resource-line/resource
                     ;;   {:resource/id
                     ;;    #uuid "d771d4d9-34de-e911-b085-00155de0a811"}}]

                                        ;:resources/start (tf/unparse (tf/formatters :date) (tt/now))
                                        ;:resources/end
                     ;; (tf/unparse (tf/formatters :date)
                     ;;                             (tt/plus (tt/now) (tt/weeks 3)))
                     })
   
   :initLocalState (fn [this props]
                     {:active-index -1
                      :all? false})
   :componentDidMount (fn [this]
                        (let [state-map (comp/component->state-map this)
                              resource-options (:resource/options state-map )
                              resource (get-in state-map [:component/id :session :account/resource])
                              teams (vec (vals (:team/id (comp/props this))))]
                          (js/console.log "teams " (comp/props this))

                          (comp/transact! this [(set-workplan-date {:start
                                                                    (str (t/date))

                                                                    ;; (tf/unparse (tf/formatters :date) (tt/now))
                                                                    :end
                                                                    (str (t/+ (t/date) (t/new-period 3 :weeks)))

                                                                    ;; (tf/unparse (tf/formatters :date)
                                                                    ;;                  (tt/plus (tt/now) (tt/weeks 3)))
                                                                    })

                                                ])

                          #_(merge/merge-component! SPA ResourcesCheckboxes
                                                  {:list/items (:resource/options (comp/props this))
                                                   :list/all-checked? false
                                                   :list/show-more? false})

                          
                          #_(doseq [team teams]
                            (do (js/console.log "VALUE" team)
                                (merge/merge-component! SPA TeamCheckbox (assoc team :ui/checked? false))) ))

                        
                        )
   
   }
  (let [resources-options (:resource/options (comp/props this))
        marker (get props [df/marker-table :projects] )
        active-resources (vec (filter (fn [m] (:resource/active? m))
                                      (vals (:checkbox/id (comp/props this)))))
        teams (vec (vals (:team/id (comp/props this))))
        current-state (uism/get-active-state this ::session/session)
        logged-in? (= :state/logged-in current-state)
        active-index (comp/get-state this :active-index)
        handleClick (fn [e v]
                      (let [active-index (comp/get-state this :active-index)
                            index (.-index  v)
                            new-index (if (= active-index index) -1 index)]
                        (comp/update-state! this assoc  :active-index new-index)
                        ))]


    

    
    
    (if (and dates logged-in?)
      
      [

       (ui-grid-column {:width 3 :style {:color "#3281b9"}}
                       (div {:style {}}
                            (ui-accordion
                             {:as Menu  :vertical true :style {:color "#3281b9"}}
                             (ui-menu-item {}
                                           (ui-accordion-title
                                            {:active (= active-index 0)
                                             :content "Dates"
                                             :index 0
                                             :style {:color  "#3281b9"}
                                             :onClick handleClick})
                                           (ui-accordion-content {:active (= active-index 0)
                                                                  :content
                                                                  (ui-form {}
                                                                           (ui-form-group {:grouped true}

                                                                                          (ui-form-field {}
                                                                                                         (dom/label {:style {:color "#3281b9"}}
                                                                                                                    "Start")
                                                                                                         (dom/input {:type "date" :size "mini"  
                                                                                                                     :onChange (fn [event data]

                                                                                                                                 (comp/transact! this [(set-workplan-date {:start (.-value (.-target event))}) :workplan/resource-lines])
                                                                                                                                 (doseq [resource resource-lines]
                                                                                                                                   (comp/transact! this [(set-totals {:resource-line-id (:resource-line/id resource)})]))


                                                                                                                                 )
                                                                                                                     :value (:start dates)
                                                                                                                     :style {:fontSize "85%" :color "#3281b9"}}))


                                                                                          

                                                                                          (ui-form-field {}
                                                                                                         (dom/label {:style {:color "#3281b9"}}"End")
                                                                                                         (dom/input {:type "date" :size "mini" 
                                                                                                                     :value (:end dates)
                                                                                                                     :onChange (fn [event data]

                                                                                                                                 (comp/transact! this [(set-workplan-date {:end (.-value (.-target event))}):workplan/resource-lines])
                                                                                                                                 (doseq [resource resource-lines]
                                                                                                                                   (comp/transact! this [(set-totals {:resource-line-id (:resource-line/id resource)})]))

                                                                                                                                 )
                                                                                                                     :style {:fontSize "85%" :color "#3281b9"}})
                                                                                                         ))

                                                                           )
                                                                  
                                                                  
                                                                  
                                                                  } ))
                             (ui-menu-item {} (ui-accordion-title
                                               {:active (= active-index 1)
                                                :content "Resources"
                                                :index 1
                                                :onClick handleClick
                                                :style {:color "#3281b9"}})
                                           (ui-accordion-content {:active (= active-index 1)
                                                                  :content (ui-form {}
                                                                                    (ui-form-group {:grouped true}
                                                                                                   (ui-form-field {}                                                                                                                                                                                                                         
                                                                                                                  (ui-resources-checkboxes {:list/items active-resources
                                                                                                                                            :list/all-checked? false})  )
                                                                                                   (ui-form-field {}                                                                                                                                                                                                                         
                                                                                                                  )))}))
                             (ui-menu-item {} (ui-accordion-title
                                               {:active (= active-index 2)
                                                :content "Teams"
                                                :index 2
                                                :onClick handleClick
                                                :style {:color "#3281b9"}})
                                           (ui-accordion-content {:active (= active-index 2)
                                                                  :content (ui-form {}
                                                                                    (ui-form-group {:grouped true}
                                                                                                   (ui-form-field {}                                                                                                                                                                                                                         
                                                                                                                  (map #(ui-team-checkbox  %) (remove #(nil? (:team/name %)) team-checkboxes)))
                                                                                                   ))})))))
       
       (ui-grid-column {:width 13}
                       (when (seq resource-lines)
                         (div {:style {:position "relative"}}

                              (div  {:style {:marginLeft "1px"
                                             :overflowX "scroll"
                                             :overflowY "visible"
                                             :paddingBottom "5px"
                                             :width "920px"
                                             :height "800px"}}
                                    
                                    #_{:style {:overflowX "auto"  :overflowY "auto" :max-height "1000px" :max-width "1000px" :position "sticky" :top 0}}
                                    (ui-table {:style {:fontSize "90%"
                                                       :position "relative"
                                                       
                                                       
                                                       } :celled true :striped true}
                                              (ui-table-header
                                               {:fullWidth true :style {:position "sticky" :top 0}}
                                               (ui-table-row
                                                {:style {:backgroundColor "red"}}

                                                (map #(ui-table-header-cell {:style {:backgroundColor "#3281b9" :color "#ffffff" :position "sticky" :top 0}} %) ["Resource" "Project" "Assignement "])

                                                (map #(ui-table-header-cell {:style {:font-weight "normal":text-align "center" :vertical-align "center" :backgroundColor "#3281b9" :color "#ffffff"
                                                                                     :position "sticky" :top 0}} %) (generate-row-dates-readable (:start dates) (:end dates)))
                                                ))
                                              (ui-table-body {} (map ui-resource-line (sort-by #(get-in  % [:resource-line/resource :resource/name]) resource-lines))))))))]

      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))
    ))




                                        ;(dr/change-route)

(dr/defrouter TopRouter [this props]
  {:router-targets [Main  Signup SignupSuccess WorkDay WorkPlan users/AdminUsers teams/Teams]})

(def ui-top-router (comp/factory TopRouter))

(defsc Session
  "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
  [this {:keys [:session/valid? :account/name] :as props}]
  {:query         [:session/valid? :account/name :account/resource]
   :ident         (fn [] [:component/id :session])
   :pre-merge     (fn [{:keys [data-tree]}]
                    (merge {:session/valid? false :account/name "" :account/resource nil}
                           data-tree))
   :initial-state {:session/valid? false :account/name "" :account/resource nil}})




(def ui-session (comp/factory Session))



(defsc TopChrome [this {:root/keys [router current-session login]}]
  {:query         [{:root/router (comp/get-query TopRouter)}
                   {:root/current-session (comp/get-query Session)}
                   [::uism/asm-id ::TopRouter]
                   {:root/login (comp/get-query Login)}]
   :ident         (fn [] [:component/id :top-chrome])
   :initial-state {:root/router          {}
                   :root/login           {}
                   :root/current-session {}}
                                        ;:shouldComponentUpdate (fn [_ _ _] true)
   }
  (let [current-tab (some-> (dr/current-route this this) first keyword)]
    (ui-grid { :container true  :divided false :columns 2 } 
             (ui-grid-row  {:strechted true}
                           (ui-menu {:style {:width "100%"} :stackable true :size "tiny" :floated true}
                                    (ui-menu-item {}
                                                  (dom/img {:src "fluxym.png" :avatar false :size "mini" :inline true :style {:marginLeft "15px"}} ))
                                    (ui-menu-item {:name "WorkPlan" :active (= :workplan current-tab) :onClick (fn [event]
                                                                                                                 (dr/change-route this (dr/path-to  WorkPlan)))} )
                                    
                                    (ui-menu-item {:name "Calendar" :active (= :main current-tab) :onClick (fn [event] (dr/change-route this ["main"]))} )
                                    
                                    (ui-menu-item {:position :right :active (or (= current-tab :admin-teams)
                                                                                (= current-tab :admin-users)) :content (ui-dropdown {:item true :text "Admin"}
                                                                                                                                    (ui-dropdown-menu {}
                                                                                                                                                      (ui-dropdown-item {:onClick #(dr/change-route this (dr/path-to users/AdminUsers))} "Users")
                                                                                                                                                      (ui-dropdown-item {:onClick #(dr/change-route this (dr/path-to teams/Teams))} "Teams")))}
                                                  )

                                    
                                    (ui-menu-item {:style {:borderLeft "1px solid #e8e9e9"} :name "Login"}
                                                  (ui-login login))
                                    )
                           
                           )
             (ui-grid-row {}
                          (ui-top-router router)))))

(def ui-top-chrome (comp/factory TopChrome))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
   (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
   (net/wrap-fulcro-request)))

#_(defsc Root [_ {:root/keys [work-day]}]
    {:query         [{:root/work-day (comp/get-query WorkDay)}]
     :initial-state {:root/work-day {}}}

    (div :.ui.container.segment
         (h3 "Inventory Items")
         (ui-work-day work-day)))

(defsc Root [this {:root/keys [top-chrome]}]
  {:query         [{:root/top-chrome (comp/get-query TopChrome)}]
   ;; :componentDidMount (fn [this] (df/load! this  :work-day/all-work-lines
   ;;                                         WorkLine
   ;;                                         {:target [:component/id :work-day :work-day/all-work-lines]
   ;;                                          :refresh [:work-day/all-work-lines]})
   ;;                                      ;(comp/transact! [()] :work-day/all-work-lines)
   ;;                      )
   :initial-state {:root/top-chrome {}}}
  

  (ui-top-chrome top-chrome)
  )

;; (defonce SPA (app/fulcro-app
;;               {;; This ensures your client can talk to a CSRF-protected server.
;;                ;; See middleware.clj to see how the token is embedded into the HTML
;;                :remotes {:remote (net/fulcro-http-remote
;;                                   {:url                "/api"
;;                                    :request-middleware secured-request-middleware})}

;;                :client-did-mount (let [;WorkLine (comp/registry-key->class :app.ui.root/WorkLine)
;;                                        project-class (comp/registry-key->class :app.ui.root/Project)]

;;                                    (fn [app]
;;                                      (df/load app :work-day/all-work-lines
;;                                               WorkLine
;;                                               {:target [:component/id :work-day :work-day/all-work-lines]})

;;                                      (df/load app :project/all-projects
;;                                               project-class
;;                                               {:post-mutation `work-line/create-project-options})))}))






(def r

  [#:assignment{:id 17592186055123,
                :name "model domain",
                :day #inst "2019-12-05T00:00:00.000-00:00",
                :work 8.0}
   #:assignment{:id 17592186055138,
                :name "model domain",
                :day #inst "2019-12-10T00:00:00.000-00:00",
                :work 8.0}
   #:assignment{:id 17592186055135,
                :name "model domain",
                :day #inst "2019-12-09T00:00:00.000-00:00",
                :work 8.0}
   #:assignment{:id 17592186055126,
                :name "model domain",
                :day #inst "2019-12-06T00:00:00.000-00:00",
                :work 8.0}
   #:assignment{:id 17592186055120,
                :name "model domain",
                :day #inst "2019-12-04T00:00:00.000-00:00",
                :work 6.0}
   #:assignment{:id 17592186055141,
                :name "model domain",
                :day #inst "2019-12-11T00:00:00.000-00:00",
                :work 2.0}
   #:assignment{:id 17592186055132,
                :name "model domain",
                :day #inst "2019-12-08T00:00:00.000-00:00",
                :work 0.0}
   #:assignment{:id 17592186055129,
                :name "model domain",
                :day #inst "2019-12-07T00:00:00.000-00:00",
                :work 0.0}])

(comment
  (reduce (fn [r [k v]] (map (fn [date] ()) dates )) (group-by #(select-keys % [:assignment/name]) r))



  (def v '[{:assignment/id 17592186053305, :assignment/name "Configuration of the solution", :assignment/day #inst "2019-12-09T00:00:00.000-00:00", :assignment/work 8} {:assignment/id 17592186053284, :assignment/name "Configuration of the solution", :assignment/day #inst "2019-12-02T00:00:00.000-00:00", :assignment/work 4} {:assignment/id 17592186053296, :assignment/name "Configuration of the solution", :assignment/day #inst "2019-12-06T00:00:00.000-00:00", :assignment/work 4} {:assignment/id 17592186053287, :assignment/name "Configuration of the solution", :assignment/day #inst "2019-12-03T00:00:00.000-00:00", :assignment/work 4} {:assignment/id 17592186053293, :assignment/name "Configuration of the solution", :assignment/day #inst "2019-12-05T00:00:00.000-00:00", :assignment/work 4}])



  (map


   (fn [r-reduce]
     (reduce (fn [r date]
               (reduce (fn [r2 m]
                         (if (=  date (first (keys m)))
                           (conj r m)
                           (conj r {date 0 :assignment/name (:assignment/name m)}))) [] r-reduce))   [] (map #(js/Date. %) dates)))


   (map
    (fn [[k vals]]
      (reduce (fn [r x] (let [day (:assignment/day x)
                              work (:assignment/work x)
                              m {day work :assignment/name k}]
                          (conj r m)

                          )) [] vals)) (group-by :assignment/name r)))

                                        ;{#inst .... 3}




  (map (fn [r [k v]] (conj r "s")) (group-by :assignment/name r))

  ;; (reduce (fn [r m]
  ;;           (concat r (reduce (fn [_ date]
  ;;                               (if (not (= date (first (keys m))))
  ;;                                 (conj r {date 0})
  ;;                                 (conj r {date (first (vals m))})))
  ;;                             [] (map #(js/Date. %) dates))))    [] r-reduce)


  )








(def r-reduce '({#inst "2019-12-09T00:00:00.000-00:00" 8} {#inst "2019-12-02T00:00:00.000-00:00" 4} {#inst "2019-12-06T00:00:00.000-00:00" 4} {#inst "2019-12-03T00:00:00.000-00:00" 4} {#inst "2019-12-05T00:00:00.000-00:00" 4}))

(def step-r (map
             (fn [[k vals]]
               (reduce (fn [r x] (let [day (:assignment/day x)
                                       work (:assignment/work x)
                                       m {day work :assignment/name k}]
                                   (conj r m)

                                   )) [] vals)) (group-by :assignment/name r)))






(reduce (fn [works date] (if (not (contains? (map (comp first keys) (first step-r)) date))
                           (conj r {date 0} )) ) (first step-r) dates2)




#_(map (comp first keys) (first step-r))



                                        ;(tt/plus (tf/parse (tf/formatter "yyyy-mm-dd") "2019-12-10") (tt/days 1))
                                        ;(tf/unparse (tf/formatter "yyyy-mm-dd") r)



