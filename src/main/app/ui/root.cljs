(ns app.ui.root
  (:require
   [app.model.session :as session]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [app.model.work-line :as work-line]
   [clojure.string :as str]
   [app.math :as math]
   ["react-number-format" :as NumberFormat]
   ["semantic-ui-calendar-react" :as SemanticUICalendar]
   [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.dom.events :as evt]
                                        ;[com.fulcrologic.fulcro.application :refer app]
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
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [taoensso.timbre :as log]))

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

(defsc Project [this {:project/keys [id] :as props}]
  {:query [:project/id :project/name fs/form-config-join]
   :ident :project/id
   :form-fields #{:project/id}})

(defsc Task [_ _]
  {:query [:task/id :task/name]
   :ident :task/id})

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

#_(defsc WorkLine [this {:ui/keys [new? saving?]
                         :work-line/keys [id]
                         :as props}]
    {:query [:ui/new?
             :ui/saving?
             :work-line/id :work-line/hours
           ;{:work-line/project (comp/get-query Project)}
           ;[:project/project-options '_]

             fs/form-config-join]

     :form-fields #{:work-line/hours}

     :pre-merge (fn [{:keys [data-tree current-normalized]}]  (fs/add-form-config WorkLine data-tree))

     :ident :work-line/id}

    (let [project-options (get props :project/project-options)]

                                        ;(js/console.log "hello" project)
      (do
;      (js/console.log "project.." project)

        (tr
         #_(td
            (ui-dropdown
             {:options project-options
                                        ;:value (second project)
              :search true
              :onChange (fn [evt data]
                          (comp/transact! this [(fs/mark-complete! {:field :work-line/hours})
                                                :item-list/all-items])
                          (m/set-value! this :work-line/project [:project/id (.-value data)]))
              :value (:project/id project)}))

         (table-cell-field this :work-line/hours {:validation-message "Quantity must be 0 or more."
                                                  :type               "number"
                                                  :value-xform        str
                                                  :onChange           (fn [evt]
                                                                        #_(comp/transact! this [(set-hours {:new-hours ()})])
                                                                        (m/set-integer! this :work-line/hours :event evt)
                                                                      ;(comp/transact! this [:work-day/all-work-lines])
                                                                        )})
         (td
          (let [visible? (or new? (log/spy :info (fs/dirty? props)))]
            (when visible?
              (div :.ui.buttons
                   (button :.ui.inline.primary.button
                           {:classes  [(when saving? "loading")]
                            :disabled (= :invalid  (log/spy :info (work-line/work-line-validator  props)))
                            :onClick  (fn []
                                        (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                          (comp/transact! this [(work-line/try-save-work-line {:item/id id :diff diff})])))} "Save")
                   (button :.ui.inline.secondary.button
                           {:onClick (fn [] (comp/transact! this [(fs/reset-form! {})]))}
                           "Undo")))))))))

(def ui-number-format (interop/react-factory NumberFormat))

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

#_(defsc ItemCategory [this {:keys [:category/id] :as props}]
    {:query       [:category/id
                   fs/form-config-join]
     :form-fields #{:category/id}
     :ident       :category/id})

#_(defsc ItemListItem [this {:ui/keys   [new? saving?]
                             :item/keys [id category]
                             :as        props}]
    {:query       [:ui/new?
                   :ui/saving?
                   :item/id :item/title :item/in-stock :item/price
                   {:item/category (comp/get-query ItemCategory)}
                   [:category/options '_]
                   fs/form-config-join]
     :form-fields #{:item/title :item/in-stock :item/price :item/category}
     :pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config ItemListItem data-tree))
     :ident       :item/id}
    (let [category-options (get props :category/options)]
      (tr
       (table-cell-field this :item/title {:validation-message "Title must not be empty"
                                           :onChange           #(m/set-string! this :item/title :event %)})
       (td
        (ui-dropdown {:options  category-options
                      :search   true
                      :onChange (fn [evt data]
                                  (comp/transact! this [(fs/mark-complete! {:field :item/title})
                                                        :item-list/all-items])
                                  (m/set-value! this :item/category [:category/id (.-value data)]))
                      :value    (:category/id category)}))
       (table-cell-field this :item/in-stock {:validation-message "Quantity must be 0 or more."
                                              :value-xform        str
                                              :type               "number"
                                              :onChange           #(m/set-integer! this :item/in-stock :event %)})
       (table-cell-field this :item/price {:validation-message "Price must be a positive amount."
                                           :input-tag          ui-money-input
                                           :onChange           #(m/set-value! this :item/price %)})

       (td
        (let [visible? (or new? (fs/dirty? props))]
          (when visible?
            (div :.ui.buttons
                 (button :.ui.inline.primary.button
                         {:classes  [(when saving? "loading")]
                          :disabled (= :invalid (item/item-validator props))
                          :onClick  (fn []
                                      (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                        (comp/transact! this [(item/try-save-item {:item/id id :diff diff})])))} "Save")
                 (button :.ui.inline.secondary.button
                         {:onClick (fn [] (if new?
                                            (comp/transact! this [(item/remove-item {:item/id id})])
                                            (comp/transact! this [(fs/reset-form! {})])))}
                         "Undo"))))))))

#_(def ui-item-list-item (comp/factory ItemListItem {:keyfn :item/id}))
#_(defsc ItemList [this {:item-list/keys [all-items] :as props}]
    {:query         [{:item-list/all-items (comp/get-query ItemListItem)}]
     :initial-state {:item-list/all-items []}
     :ident         (fn [] [:component/id ::item-list])
     :route-segment ["work-day"]}
    (table :.ui.table
           (thead (tr (th "Title") (th "Category") (th "# In Stock") (th "Price") (th "Row Action")))
           (tbody (map ui-item-list-item all-items))
           (tfoot (tr (th {:colSpan 5}
                          (button :.ui.primary.icon.button
                                  {:onClick (fn []
                                              (merge/merge-component! this ItemListItem
                                                                      {:ui/new?       true
                                                                       :item/id       (tempid/tempid)
                                                                       :item/title    ""
                                                                       :item/in-stock 0
                                                                       :item/price    (math/bigdecimal "0")}
                                                                      :append [:component/id :app.client/item-list :item-list/all-items]))}
                                  (dom/i :.plus.icon)))))))

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

(defsc WorkLine [this {:ui/keys   [new? saving?]
                       :work-line/keys [id]
                       :as        props}]
  {:query       [:ui/new?
                 :ui/saving?
                 :work-line/id :work-line/hours
;                 {:item/category (comp/get-query ItemCategory)}
 ;                [:category/options '_]
                 fs/form-config-join]
   :form-fields #{:work-line/hours}
   :pre-merge   (fn [{:keys [data-tree]}] (fs/add-form-config WorkLine data-tree))
   :ident       :work-line/id}
  (let [;category-options (get props :category/options)
        ]
    (tr
     #_(table-cell-field this :item/title {:validation-message "Title must not be empty"
                                           :onChange           #(m/set-string! this :item/title :event %)})
     #_(td
        ;; (ui-dropdown {:options  category-options
        ;;               :search   true
        ;;               :onChange (fn [evt data]
        ;;                           (comp/transact! this [(fs/mark-complete! {:field :item/title})
        ;;                                                 :item-list/all-items])
        ;;                           (m/set-value! this :item/category [:category/id (.-value data)]))
        ;;               :value    (:category/id category)})
        )
     (table-cell-field this :work-line/hours {:validation-message "Quantity must be 0 or more."
                                              :value-xform        str
                                              :type               "number"
                                              :onChange           #(m/set-integer! this :work-line/hours :event %)})
     #_(table-cell-field this :work-line/hours
                         {:validation-message "Hours must be a positive amount."
;                                          :input-tag          ui-money-input
                          :onChange           #(m/set-integer! this :work-line/hours %)})

     (td
      (let [visible? (or new? (fs/dirty? props))]
        (when visible?
          (div :.ui.buttons
               (button :.ui.inline.primary.button
                       {:classes  [(when saving? "loading")]
                        :disabled (= :invalid (item/item-validator props))
                        :onClick  (fn []
                                    (let [diff (fs/dirty-fields props false {:new-entity? new?})]
                                      (comp/transact! this [(item/try-save-item {:item/id id :diff diff})])))} "Save")
               (button :.ui.inline.secondary.button
                       {:onClick (fn [] (if new?
                                          (comp/transact! this [(work-line/remove-item {:work-line/id id})])
                                          (comp/transact! this [(reset-form {:id id})])))}
                       "Undo"))))))))

(def ui-work-line (comp/factory WorkLine {:keyfn :work-line/id}))

(defsc WorkDay [this {:work-day/keys [all-work-lines] :as props}]
  {:query         [{:work-day/all-work-lines (comp/get-query WorkLine)}]
   :initial-state {:work-day/all-work-lines []}
   :ident         (fn [] [:component/id :work-day])
   :route-segment ["work-day"]}
  
  (table :.ui.table
         (thead (tr (th "Hours") ;(th "Category") (th "# In Stock") (th "Price") (th "Row Action")
                    ))
         (tbody (map ui-work-line all-work-lines))
         (tfoot (tr (th {:colSpan 5}
                        (button :.ui.primary.icon.button
                                {:onClick (fn []
                                            (merge/merge-component! this WorkLine
                                                                    {:ui/new?       true
                                                                     :work-line/id       (tempid/tempid)
                                                                     :work-line/hours 0}
                                                                    :append [:component/id :work-day :work-day/all-work-lines]))}
                                (dom/i :.plus.icon)))))))

#_(defsc WorkDay [this {:work-day/keys [all-work-lines] :as props}]
    {:query         [{:work-day/all-work-lines (comp/get-query WorkLine)}]
     :initial-state {:work-day/all-work-lines []}
     :ident         (fn [] [:component/id :work-day])

     :route-segment ["work-day"]}
  ;; TODO: work on this...
    (let [total (reduce
                 (fn [amt {:work-line/keys [hours]}]
                   (+ amt hours))
                 0
                 all-work-lines)]
      (table :.ui.table
             (thead (tr (th "Project")  (th "Hours")  (th "Row Action")))
             (tbody (map ui-work-line  all-work-lines))
             (tfoot (tr
                                        ;(th "") (th "") (th "") (th "")
                     (th "") (th "Total hours: ") (th total) (th "")
                     (th
                      (button :.ui.primary.icon.button
                              {:onClick (fn []
                                          (merge/merge-component! this WorkLine
                                                                  {:ui/new? true
                                                                   :ui/saving? false
                                                                   :work-line/project {:project/id 3}
                                                                   :work-line/hours 22
                                        ;:work-line/project {:project/id 3 :project/name "Something"}
                                                                   :work-line/id (tempid/tempid)
                                        ;:work-line/project
                                                                   }
                                                                  :append [:component/id :work-day :work-day/all-work-lines]))}

                              (dom/i :.plus.icon)))))))
  ;; TODO maybe have it stored as decimal instead of string ...
    )
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

(defmutation load-all-work-lines [params]
  (action [{:keys [state app] :as params}]
          (cljs.pprint/pprint (keys params))

          #_(swap! state merge/merge-component WorkLine sample-server-response
                   :append [:component/id :work-day :work-day/all-work-lines])
          #_(swap! state (fn [s]
                           (merge/merge-component! s component-query sample-server-response)))))

#_(defmutation load-all-work-lines [_]
    (action [{:keys [state ref app]}]
            (-> state
                (swap! merge/merge-component WorkLine {:work-line/id 13

                                                       :work-line/project
                                                       {:project/id 2 :project/name "Hello"}

                                                       :work-line/hours 22}

                       #_{:work-line/id 1

                          :work-line/project
                          {:project/id 2 :project/name "Hello"}

                          :work-line/hours 3}

                       :append [:component/id :work-day :work-day/all-work-lines]))

            #_(df/load! app :work-day/all-work-lines WorkLine {:target [:component/id :work-day :work-day/all-work-lines]})
            (dr/change-route app ["work-day"])))

(fs/reset-form!)
(defsc Date [this {:date/keys [selected-day]}]
  {:query         [:date/selected-day]
   :initial-state (fn [_] {:date/selected-day "14-11-2019"})
   :ident         (fn [] [:component/id :date])
                                        ;:route-segment ["main"]
   }

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
                   :onChange (fn []
                               #_(comp/transact! this [(load-all-work-lines) ;:work-day/all-work-lines
                                                       ])
                               #_(merge/merge-component! this WorkLine
                                                         [{:work-line/hours 1
                                                           :ui/new false
                                        ;:work-line/project {:project/id 3 :project/name "Something"}
                                                           :work-line/id (tempid/tempid)
                                        ;:work-line/project
                                                           }]
                                                         :append [:component/id :work-day :work-day/all-work-lines])

                               #_(df/load! this :work-day/all-work-lines WorkLine {:target [:component/id :work-day :work-day/all-work-lines]})
                               #_(comp/transact! this [(configure {})])
                               (dr/change-route this ["work-day"])


;                               (comp/transact! this [(load-all-work-lines)])
                               )
                                        ;(fn [evnt data] (js/console.log data))
                   })
                                        ;(ui-calendar {})
  )

(def ui-date (comp/factory Date))

(defsc Main [this {:main/keys [date welcome-message]}]
  {:query         [:main/welcome-message {:main/date (comp/get-query Date)}]
   :initial-state (fn [_] {:main/welcome-message "Hi!"
                           :main/date (comp/get-initial-state Date {})})
   :ident         (fn [] [:component/id :main])
   :route-segment ["main"]}

  (js/console.log "Date" date)
  (ui-date date))

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
                                           (div :.ui.message
                                                (dom/p "Don't have an account?")
                                                (dom/a {:onClick (fn []
                                                                   (uism/trigger! this ::session/session :event/toggle-modal {})
                                                                   (dr/change-route this ["signup"]))}
                                                       "Please sign up!"))))))))))))

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

  (js/console.log "Date" date)
  (ui-date date))

(defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
  {:query         [:account/time-zone :account/real-name]
   :ident         (fn [] [:component/id :settings])
   :route-segment ["settings"]
   :initial-state {}}
  (div :.ui.container.segment
       (h3 "Settings")))

(dr/defrouter TopRouter [this props]
  {:router-targets [Main Login Signup SignupSuccess WorkDay]})

(def ui-top-router (comp/factory TopRouter))

(defsc Session
  "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
  [this {:keys [:session/valid? :account/name] :as props}]
  {:query         [:session/valid? :account/name]
   :ident         (fn [] [:component/id :session])
   :pre-merge     (fn [{:keys [data-tree]}]
                    (merge {:session/valid? false :account/name ""}
                           data-tree))
   :initial-state {:session/valid? false :account/name ""}})

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
              (dom/a :.item {:classes [(when (= :main current-tab) "active")]
                             :onClick (fn [] (dr/change-route this ["main"]))} "Main")
              (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
                             :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
              (div :.right.menu
                   (ui-login login)))
         (div :.ui.grid
              (div :.ui.row
                   (ui-top-router router)
                   #_(ui-work-day))))))

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
     :componentDidMount (fn [this] (df/load! this  :work-day/all-work-lines
                                             WorkLine
                                             {:target [:component/id :work-day :work-day/all-work-lines]
                                              :refresh [:work-day/all-work-lines]})
                        ;(comp/transact! [()] :work-day/all-work-lines)
                          )
     :initial-state {:root/top-chrome {}}}

    (ui-top-chrome top-chrome))

#_(defonce SPA (app/fulcro-app
                {;; This ensures your client can talk to a CSRF-protected server.
                 ;; See middleware.clj to see how the token is embedded into the HTML
                 :remotes {:remote (net/fulcro-http-remote
                                    {:url                "/api"
                                     :request-middleware secured-request-middleware})}

                 :client-did-mount (let [;WorkLine (comp/registry-key->class :app.ui.root/WorkLine)
                                         project-class (comp/registry-key->class :app.ui.root/Project)]

                                     (fn [app]
                                       (df/load app :work-day/all-work-lines
                                                WorkLine
                                                {:target [:component/id :work-day :work-day/all-work-lines]})

                                       (df/load app :project/all-projects
                                                project-class
                                                {:post-mutation `work-line/create-project-options})))}))

(comment

  (as-> {} state
    (merge/merge* state component-query sample-server-response)
                                        ; (assoc state :k 1)
    (cljs.pprint/pprint state)))
