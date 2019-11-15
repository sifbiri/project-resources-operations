(ns app.ui.root
  (:require
    [app.model.session :as session]
    [clojure.string :as str]
    [app.math :as math]
    ["semantic-ui-calendar-react" :as SemanticUICalendar]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
    [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
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


(def ui-date-input (interop/react-factory SemanticUICalendar/DateInput))

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
                   :onChange (fn [] (dr/change-route this ["work-day"]))
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


(defsc Project [_ _]
  {:query [:project/id]
   :indent :project/id}
  :form-fields #{:category/id}
  :ident :category/id)

(defsc Task [_ _]
  {:query [:task/id :task/name]
   :indent :task/id})

(defsc WorkLine [this {:ui/keys [new? saving?]
                       :work-line/keys [id project]
                       :as props}
                 ]
  {:query [:ui/new?
           :ui/saving?
           :work-line/id
           {:work-line/project (comp/get-query Project)}
           :work-line/hours
           [:project/options '_]
           fs/form-config-join]

   :form-fields #{:work-line/hours :work-line/projec}

   :pre-merge (fn [{:keys [data-tree]}] (fs/add-form-config WorkLine data-tree))

   :indent :work-line/id
   }
  (let [project-options (get props :project/options)]
    (tr
     (td
      (ui-dropdown
       {:options project-options
        :search true
        :onChange (fn [evtork data]
                    (m/set-value! this :work-line/project [:project/id (.-value data)]))})))))


(def ui-work-line (comp/factory WorkLine))
(defsc WorkDay [this {:work-day/keys [all-work-lines]}]
  {:query         [{:work-day/all-work-lines (comp/get-query WorkLine) }]
   :initial-state {:work-day/all-work-lines []}
   :ident         (fn [] [:component/id :work-day])
   :route-segment ["work-day"]
   }
  (let [total (reduce
                (fn [amt {:item/keys [hours]}]
                  (+ amt hours))
                0
                all-work-lines)]
     (table :.ui.table
            (thead (tr (th "Project") (th "Task") (th "Hours")  (th "Row Action")))
            (tbody (map ui-work-line  all-work-lines))
            (tfoot (tr
                                        ;(th "") (th "") (th "") (th "")
                    (th "") (th "Total hours: ") (th total) (th "")
                    (th
                     (button :.ui.primary.icon.button
                             {:onClick (fn []
                                         (merge/merge-component! this WorkLine
                                                                 {:ui/new?       true
                                                                  :item/id       (tempid/tempid)
                                                                  :item/title    ""
                                                                  :item/in-stock 0
                                                                  :item/price    (math/bigdecimal "0")}
                                                                 :append [:component/id
                                                                          :item-list/all-items]))}
                             (dom/i :.plus.icon)))))))
  ;; TODO maybe have it stored as decimal instead of string ...
  )



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
  {:router-targets [Main Signup SignupSuccess Settings WorkDay]})

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
          (ui-top-router router))))))

(def ui-top-chrome (comp/factory TopChrome))

(defsc Root [this {:root/keys [top-chrome]}]
  {:query         [{:root/top-chrome (comp/get-query TopChrome)}]
   :initial-state {:root/top-chrome {}}}
  (ui-top-chrome top-chrome))
