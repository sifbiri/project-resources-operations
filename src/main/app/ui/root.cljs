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


   [app.model.project :as project]
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
                    })
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



    (dom/div
     (when-not initial?
       (dom/div :.right.menu
                (if logged-in?
                  (dom/button :.item
                              {:onClick #(uism/trigger! this ::session/session :event/logout)}
                              (dom/span current-user) ent/nbsp "Log out")

                  (dom/div :.item {:style   {:position "relative"}
                                   :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
                           "Login"
                           (when open?
                             (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
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
                                                         ))))))))))))

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

(defsc Resource
  [this {:resource/keys [id name email-address]}]
  {:query [:resource/id :resource/name :resource/email-address ]
   :ident :resource/id}
  #_(let
        [resources-options (:resource/options (comp/props this))]



      (dom/div
       (ui-dropdown {:placeholder "Select Resource"
                     :options  options
                     :search   true
                     :onChange (fn [evt data] (println (.-value data)
                                                       )
                                 (m/set-value! this :resource/id  (.-value data))



                                 )
                     :value id})
       ;;

       #_(when (:resource/id resource)
           (p "hello there")))))



(defsc Project
  [this {:project/keys [id name]}]
  {:query [:project/id :project/name]
   :ident :project/id})

(defsc SelectedProject
  [this {:selected/keys [project]}]
  {:query [:selected/project (comp/get-query Project)
           {:selected/resource (comp/get-query Resource)}]
   :ident (fn [] [:component/id :selected])}


  (dom/p {} "sd"))

(def ui-selected-project (comp/factory SelectedProject))

(def ui-selected-comp (comp/factory SelectedProject))

(defn ui-selected [{:selected/keys [project resource]}]

  (dom/p (:project/name project)))


(def ui-resource (comp/factory Resource))






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
                 :target [:component/id :resources :resources/assignments]})))


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
               (conj r (str (str/capitalize day-of-week) ", " (str (str/capitalize month) ", " day-of-month) " " year)))))))





(defsc ProjectLine [this {:keys [project-line/id
                                 project-line/resource
                                 project-line/assignments
                                 project-line/project
                                 ui/selected
                                 ui/dates] :as props}]
  {:query [:project-line/id
           [:ui/dates '_]
           {:project-line/project (comp/get-query Project)}
           {:project-line/resource (comp/get-query Resource)}
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
    (js/console.log "transformed" transformed)
    (when (> total 0 )
      (comp/fragment

       (tr
        {:style {}
         :onClick (fn []
                    #_(comp/set-state! this {:ui/selected (not (:ui/selected (comp/get-state this)))})
                    (m/toggle! this :ui/selected)
                    )}

        (td "") (td (str (:project/name project) " ")
                    (when (not selected) (ui-icon {:name "angle down" :link true :style {:display "inline"}})))

        (td "")

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
                   (tr (concat [(td "")(td "") (td (:assignment/name (first asses)))]
                               (map #(td {:style {:background-color (color (first (vals %)))}}
                                         (goog.string.format "%.2f" (first (vals %)))) asses))))
                 )
               transformed)))))))

(def ui-project-line (comp/factory ProjectLine {:keyfn :project-line/id}))


(defmutation set-total [{:keys [resource project  id]}]
  (action [{:keys [state] :as env}]

          (js/console.log "STATEIS2" @state)
          ))


(defsc ResourceLine [this {:resource-line/keys [resource project-lines]
                           :keys [ui/selected ui/dates]
                           :as props}]
  {:query
   [:ui/selected
    {:resource-line/resource (comp/get-query Resource)}
    [:ui/dates '_]
    :resource-line/id
    {:resource-line/projects (comp/get-query Project)}
    {:resource-line/project-lines (comp/get-query ProjectLine)}
    ]
   :initLocalState (fn [this props] {:count 0})
   :ident :resource-line/id
   :componentDidUpdate (fn [this next-props next-state]
                         (let [state (comp/component->state-map this)
                               id (:resource-line/id
                                   (comp/props this))
                               project-lines (get-in
                                              state
                                              [:resource-line/id id :resource-line/project-lines])

                               project-lines-r
                               (map (fn [ident]
                                      (get-in state ident))
                                    project-lines)

                               ;; TODO resolve projects 
                               start (:start (:dates (comp/props this)))
                               end (:end (:dates (comp/props this)))]
                           
                           (when (= (:count next-state) 0)

                             (let [transform-project-line

                                   (fn [pl]
                                     (let [r-step
                                           (map
                                            (fn [[k vals]]
                                              (reduce (fn [r x] (let [day (:assignment/day x)
                                                                      work (:assignment/work x)
                                                                      m {day work :assignment/name k}]
                                                                  (conj r m)
                                                                  
                                                                  )) [] vals))
                                            (group-by :assignment/name (:project-line/assignments pl)))]


                                       (js/console.log "STEP" r-step)
                                       (map
                                        (fn [r]
                                          (map (fn [date] (get-from-source date r) ) (map #(js/Date. %)
                                                                                          (generate-row-dates start end))))
                                        r-step
                                        


                                        )))
                                   
                                   totals
                                   (map transform-project-line  project-lines-r)]

                               
                               
                               
                               


                               (comp/update-state! this assoc :count (+ (:count state) 1) )
                               (js/console.log "TOTALS"  project-lines-r)
                               (js/console.log "STATE!"  state)))))



                             
                             

                             :componentDidMount (fn [this]
                                                  (let
                                                      [start (:start (:dates (comp/props this)))

                                                       end (:end (:dates (comp/props this)))
                                                       resource-line-id (:resource-line/id (comp/props this))
                                                       {:resource-line/keys [resource projects]}
                                                       (:resource-line/resource (comp/props this))

                                                       ]

                                                    (df/load! this :projects Project
                                                              {:marker :projects
                                                               :params               {:resource/id resource-line-id}

                                                               :target [:resource-line/id  resource-line-id :resource-line/projects]
                                        ;:post-mutation        `project/populate-projects
                                                               :post-mutation `set-projects-lines
                                                               ;; we get the resource from workplan
                                                               :post-mutation-params {:projects (:resource-line/projects (comp/props this)) :resource-id (:resource-line/id (comp/props this))}

                                                               })




                                                    (let [project-lines (get-in  [:resource-line/id  resource-line-id]

                                                                                 (comp/component->state-map this))]


                                                      (js/console.log "PROJECT-LINES" (:resource-line/id (comp/component->state-map this))))




                                                    )
                                                  )
                             


                             }

                           ;; TODO extract common functions to top




                           (comp/fragment
                            (tr
                             {:style {}
                              :onClick (fn []
                                         (m/toggle! this :ui/selected)
                                         )}

                             (td (str (:resource/name resource) " ")
                                 (when (not selected) (ui-icon {:name "angle down" :link true :style {:display "inline"}})))


                             )

                            (when selected
                              (map ui-project-line project-lines))))
                         (def ui-resource-line (comp/factory ResourceLine {:keyfn :resource-line/id}))


                         (defmutation set-project-line [{:keys [resource project  id]}]
                           (action [{:keys [state] :as env}]


                                   (swap! state assoc-in [:project-line/id id :project-line/resource] resource)
                                   (swap! state assoc-in [:project-line/id id :project-line/project] project)
                                   (swap! state assoc-in [:project-line/id id :project-line/id] id)

                                   (swap! state (df/load   :assignments Assignment
                                                           {:params               {:resource/id (:resource/id resource)
                                                                                   :project/id (:project/id project)}
                                                            :target [:project-line/id id :project-line/assignments]

                                                            })) ))






                         (defmutation set-projects-lines [{:keys [projects resource-id]}]
                           (action [{:keys [state] :as env}]

                                   (let
                                       [projects-s (get-in @state [:resource-line/id resource-id  :resource-line/projects])
                                        resource-s (get-in @state [:resource-line/id resource-id  :resource-line/resource])

                                        projects-resolved (map (fn [[_ project-id]]
                                                                 (get-in @state [:project/id project-id]))
                                                               projects-s)
                                        resource-resolved (get-in @state [:resource/id resource-id])
                                        project-lines-ids (atom [])]



                                     (doseq [c-project projects-resolved]



                                       (let [project-line-id (random-uuid)
                                        ;state-map (comp/component->state-map this)
                                             ]

                                        ;(swap! state project-lines-ids conj [:project-line/id project-line-id])




                                         #_(df/load! SPA :assignments Assignment
                                                     {:params               {:resource/id (:resource/id resource-resolved)
                                                                             :project/id (:project/id c-project)}
                                                      :target (:project-line/id project-line-id :project-line/assignments)})


                                         (swap! state merge/merge-component  ProjectLine
                                                {:project-line/resource resource-resolved
                                                 :project-line/project c-project
                                                 :project-line/id project-line-id

                                                 })

                                         (df/load! SPA :assignments Assignment
                                                   {:params               {:resource/id (:resource/id resource-resolved)
                                                                           :project/id (:project/id c-project)}
                                                    :target [:project-line/id project-line-id :project-line/assignments]})

                                         (swap! project-lines-ids conj project-line-id)



                                         #_(comp/transact! this  `[(set-project-line {:resource resource
                                                                                      :project c-project
                                                                                      :id project-line-id}   )]

                                                           )




                                         #_(df/load this  :assignments Assignment
                                                    {:params               {:resource/id (:resource/id resource)
                                                                            :project/id (:project/id c-project)}
                                                     :target [:project-line/id project-line-id :project-line/assignments]

                                                     })

                                         ))



                                     ;; TODO move loading to the level of resource line
                                     (swap! state assoc-in [:resource-line/id  resource-id  :resource-line/project-lines] (mapv (fn [id] [:project-line/id id])@project-lines-ids))
                                     (swap! state update-in [:component/id :workplan :ui/loading] not)

                                     ))

                           (ok-action [{:keys [state]}]
                                      (js/console.log "OK_ACTION" @state)
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


                           (div {:style {:overflow-x "scroll" :width "1200px" :height "1000px" :overflowY "auto" }}



                                ))


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

                         (defsc WorkPlan [this {:workplan/keys [resource-lines]
                                                :keys [ui/dates ui/loading]:as props}]
                           {:query         [{:workplan/resource-lines (comp/get-query ResourceLine)}

                                            :ui/loading
                                        ;{:workplan/projects (comp/get-query Project)}
                                        ;{:workplan/project-lines (comp/get-query ProjectLine)}
                                            [::uism/asm-id ::session/session]

                                        ;{:workplan/selected (comp/get-query SelectedProject)}

                                            [:resource/options '_]
                                            [:ui/dates '_]
                                            [df/marker-table :projects]]
                            :ident         (fn [] [:component/id :workplan])
                            :route-segment ["workplan"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
                            :will-enter (fn [app route-params]
                                          (dr/route-deferred
                                           [:component/id :workplan]
                                           (fn []
                                             (df/load! app :resource/all-resources Resource {:post-mutation `resource/create-resource-options})
                                             (comp/transact! app [(dr/target-ready {:target [:component/id :workplan]})]))))


                            :initial-state (fn [params]
                                             {

                                              :ui/loading false

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
                                              {:value [#uuid "d771d4d9-34de-e911-b085-00155de0a811"]})
                            :componentDidMount (fn [this]
                                                 (let [state-map (comp/component->state-map this)
                                                       resource (get-in state-map [:component/id :session :account/resource])]

                                                   (comp/transact! this [(set-workplan-date {:start
                                                                                             (str (t/date))

                                                                                             ;; (tf/unparse (tf/formatters :date) (tt/now))
                                                                                             :end
                                                                                             (str (t/+ (t/date) (t/new-period 3 :weeks)))

                                                                                             ;; (tf/unparse (tf/formatters :date)
                                                                                             ;;                  (tt/plus (tt/now) (tt/weeks 3)))
                                                                                             })

                                                                         ]))
                                                 )
                            }
                           (let [resources-options (:resource/options (comp/props this))
                                 marker (get props [df/marker-table :projects] )
                                 current-state (uism/get-active-state this ::session/session)
                                 logged-in? (= :state/logged-in current-state)]

                                        ;(js/console.log "resources " resources-options)

                             (if (and dates logged-in?)
                               (div
                                {:style {:overflowX "auto" :width  "1200px" :height "1000px" :overflowY "auto" }}
                                (div
                                 (ui-dropdown
                                  { :button true


                                   :labeled true

                                   :multiple true
                                   :selection true
                                   :label "Resource"
                                   :style {   :paddingLeft "8px" :paddingBottom "8px" :paddingTop "5px"
                                           :position "relative" :top "-5px" :left "-8px" :paddingRight "20px" :border-radius "2px"}

                                   :placeholder "Resource"
                                   :options  resources-options
                                   :search true

                                   :onChange (fn [evt data]

                                        ;     (comp/set-state! this {:value (.-value data)})
                                               (comp/transact! this [(set-resource-lines {:ids (.-value data)}) :workplan/resource-lines] )
                                               )

                                   :value (map #(:resource-line/id %) resource-lines)


                                   }
                                  )

                                 (ui-input {:type "date" :size "mini" :label "Start"  :style {:border "2px solid LightGray" :border-radius "5px" :margin-right "5px"}
                                            :onChange (fn [event data]

                                                        (comp/transact! this [(set-workplan-date {:start (.-value (.-target event))})])


                                                        )
                                            :value (:start dates)
                                            :action true})


                                 (ui-input {:type "date" :size "mini" :label "End"
                                            :value (:end dates)
                                            :onChange (fn [event data]

                                                        (comp/transact! this [(set-workplan-date {:end (.-value (.-target event))})])

                                                        )
                                            :style {:marginRight "15px" :border "2px solid LightGray" :borderRadius "5px"}})
                                 )


                                (if true ;loading
                                        ;(ui-loader {:active true :inline "centered"})
                                  (ui-table {:celled true}
                                            (ui-table-header {:fullWidth true}
                                                             (ui-table-row  {}

                                                                            (map #(ui-table-header-cell {} %)  ["Resource" "Project" "Assignement "])

                                                                            (map #(ui-table-header-cell {:style {:font-weight "normal":text-align "center" :vertical-align "center"}} %) (generate-row-dates-readable (:start dates) (:end dates)))
                                                                            ))
                                            (tbody (map ui-resource-line resource-lines))))

                                )
                               (ui-segment {:style {:textAlign "center"}}
                                           (div  "Please login with Fluxym account")))
                             ))




                                        ;(dr/change-route)

                         (dr/defrouter TopRouter [this props]
                           {:router-targets [Main  Signup SignupSuccess WorkDay WorkPlan]})

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
                                            :root/current-session {}}}
                           (let [current-tab (some-> (dr/current-route this this) first keyword)]
                             (div :.ui.container
                                  (div :.ui.secondary.pointing.menu
                                       (ui-image {:src "fluxym.png" :avatar false :size "mini" :inline true :style {:marginLeft "15px"}} )

                                       (dom/a :.item {:classes [(when (= :workplan current-tab) "active")]
                                                      :onClick (fn [event]


                                                                 (dr/change-route this ["workplan"]))} "WorkPlan")

                                       (dom/a :.item {:classes [(when (= :main current-tab) "active")]
                                                      :onClick (fn []
                                        ;(m/load! this )
                                                                 (dr/change-route this ["main"]))} "Calendar")

                                       (div :.right.menu
                                            (ui-login login)

                                            ))
                                  (div :.ui.centered
                                       (ui-top-router router)
                                       #_(ui-work-day)))))

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




