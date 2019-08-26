(ns app.ui.root
  (:require
    [app.model.session :as session]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as prim :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [clojure.string :as str]))

;; ;;;;;;;;;;;;;;;;
;; ;; Utils
;; ;;;;;;;;;;;;;;;;

;; (defn field [{:keys [label valid? error-message] :as props}]
;;   (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
;;     (div :.ui.field
;;       (dom/label {:htmlFor label} label)
;;       (dom/input input-props)
;;       (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
;;         error-message))))

;; ;;;;;;;;;;;;;;;;
;; ;; SignUp
;; ;;;;;;;;;;;;;;;;

;; (defsc SignupSuccess [this props]
;;   {:query         ['*]
;;    :initial-state {}
;;    :ident         (fn [] [:component/id :signup-success])
;;    :route-segment ["signup-success"]
;;    :will-enter    (fn [app _] (dr/route-immediate [:component/id :signup-success]))}
;;   (div
;;     (dom/h3 "Signup Complete!")
;;     (dom/p "You can now log in!")))

;; (defsc Signup [this {:account/keys [email password password-again] :as props}]
;;   {:query             [:account/email :account/password :account/password-again fs/form-config-join]
;;    :initial-state     (fn [_]
;;                         (fs/add-form-config Signup
;;                           {:account/email          ""
;;                            :account/password       ""
;;                            :account/password-again ""}))
;;    :form-fields       #{:account/email :account/password :account/password-again}
;;    :ident             (fn [] session/signup-ident)
;;    :route-segment     ["signup"]
;;    :componentDidMount (fn [this]
;;                         (comp/transact! this [(session/clear-signup-form)]))
;;    :will-enter        (fn [app _] (dr/route-immediate [:component/id :signup]))}
;;   (let [submit!  (fn [evt]
;;                    (when (or (identical? true evt) (evt/enter-key? evt))
;;                      (comp/transact! this [(session/signup! {:email email :password password})])
;;                      (log/info "Sign up")))
;;         checked? (log/spy :info (fs/checked? props))]
;;     (div
;;       (dom/h3 "Signup")
;;       (div :.ui.form {:classes [(when checked? "error")]}
;;         (field {:label         "Email"
;;                 :value         (or email "")
;;                 :valid?        (session/valid-email? email)
;;                 :error-message "Must be an email address"
;;                 :autoComplete  "off"
;;                 :onKeyDown     submit!
;;                 :onChange      #(m/set-string! this :account/email :event %)})
;;         (field {:label         "Password"
;;                 :type          "password"
;;                 :value         (or password "")
;;                 :valid?        (session/valid-password? password)
;;                 :error-message "Password must be at least 8 characters."
;;                 :onKeyDown     submit!
;;                 :autoComplete  "off"
;;                 :onChange      #(m/set-string! this :account/password :event %)})
;;         (field {:label         "Repeat Password" :type "password" :value (or password-again "")
;;                 :autoComplete  "off"
;;                 :valid?        (= password password-again)
;;                 :error-message "Passwords do not match."
;;                 :onChange      #(m/set-string! this :account/password-again :event %)})
;;         (dom/button :.ui.primary.button {:onClick #(submit! true)}
;;           "Sign Up")))))

;; (declare Session)

;; ;;;;;;;;;;;;;;;;
;; ;; LogIn
;; ;;;;;;;;;;;;;;;;

;; (defsc Login [this {:account/keys [email]
;;                     :ui/keys      [error open?] :as props}]
;;   {:query         [:ui/open? :ui/error :account/email
;;                    {[:component/id :session] (comp/get-query Session)}
;;                    [::uism/asm-id ::session/session]]
;;    :css           [[:.floating-menu {:position "absolute !important"
;;                                      :z-index  1000
;;                                      :width    "300px"
;;                                      :right    "0px"
;;                                      :top      "50px"}]]
;;    :initial-state {:account/email "" :ui/error ""}
;;    :ident         (fn [] [:component/id :login])}
;;   (let [current-state (uism/get-active-state this ::session/session)
;;         {current-user :account/name} (get props [:component/id :session])
;;         initial?      (= :initial current-state)
;;         loading?      (= :state/checking-session current-state)
;;         logged-in?    (= :state/logged-in current-state)
;;         {:keys [floating-menu]} (css/get-classnames Login)
;;         password      (or (comp/get-state this :password) "")] ; c.l. state for security
;;     (dom/div
;;       (when-not initial?
;;         (dom/div :.right.menu
;;           (if logged-in?
;;             (dom/button :.item
;;               {:onClick #(uism/trigger! this ::session/session :event/logout)}
;;               (dom/span current-user) ent/nbsp "Log out")
;;             (dom/div :.item {:style   {:position "relative"}
;;                              :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
;;               "Login"
;;               (when open?
;;                 (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
;;                                                                         ;; Stop bubbling (would trigger the menu toggle)
;;                                                                         (evt/stop-propagation! e))
;;                                                              :classes [floating-menu]}
;;                   (dom/h3 :.ui.header "Login")
;;                   (div :.ui.form {:classes [(when (seq error) "error")]}
;;                     (field {:label    "Email"
;;                             :value    email
;;                             :onChange #(m/set-string! this :account/email :event %)})
;;                     (field {:label    "Password"
;;                             :type     "password"
;;                             :value    password
;;                             :onChange #(comp/set-state! this {:password (evt/target-value %)})})
;;                     (div :.ui.error.message error)
;;                     (div :.ui.field
;;                       (dom/button :.ui.button
;;                         {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
;;                                                                                              :password password}))
;;                          :classes [(when loading? "loading")]} "Login"))
;;                     (div :.ui.message
;;                       (dom/p "Don't have an account?")
;;                       (dom/a {:onClick (fn []
;;                                          (uism/trigger! this ::session/session :event/toggle-modal {})
;;                                          (dr/change-route this ["signup"]))}
;;                         "Please sign up!"))))))))))))

;; (def ui-login (comp/factory Login))

;; ;;;;;;;;;;;;;;;;
;; ;; Main
;; ;;;;;;;;;;;;;;;;


;; (defsc Main [this props]
;;   {:query         [:main/welcome-message]
;;    :initial-state {:main/welcome-message "Hi!"}
;;    :ident         (fn [] [:component/id :main])
;;    :route-segment ["main"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :main]))}
;;   (div :.ui.container.segment
;;     (h3 "Main")))




;; ;;;;;;;;;;;;;;;;
;; ;; Settings
;; ;;;;;;;;;;;;;;;;

;; (defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
;;   {:query         [:account/time-zone :account/real-name]
;;    :ident         (fn [] [:component/id :settings])
;;    :route-segment ["settings"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :settings]))
;;    :initial-state {}}
;;   (div :.ui.container.segment
;;     (h3 "Settings")))

;; (dr/defrouter TopRouter [this props]
;;   {:router-targets [Main Signup SignupSuccess Settings]})

;; (def ui-top-router (comp/factory TopRouter))

;; ;;;;;;;;;;;;;;;;
;; ;; Session
;; ;;;;;;;;;;;;;;;;

;; (defsc Session
;;   "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
;;   [this {:keys [:session/valid? :account/name] :as props}]
;;   {:query         [:session/valid? :account/name]
;;    :ident         (fn [] [:component/id :session])
;;    :pre-merge     (fn [{:keys [data-tree]}]
;;                     (merge {:session/valid? false :account/name ""}
;;                       data-tree))
;;    :initial-state {:session/valid? false :account/name ""}})

;; (def ui-session (prim/factory Session))

;; ;;;;;;;;;;;;;;;;
;; ;; TopChrome
;; ;;;;;;;;;;;;;;;;

;; (defsc TopChrome [this {:root/keys [router current-session login]}]
;;   {:query         [{:root/router (comp/get-query TopRouter)}
;;                    {:root/current-session (comp/get-query Session)}
;;                    [::uism/asm-id ::TopRouter]
;;                    {:root/login (comp/get-query Login)}]
;;    :ident         (fn [] [:component/id :top-chrome])
;;    :initial-state {:root/router          {}
;;                    :root/login           {}
;;                    :root/current-session {}}}
;;   (let [current-tab (some-> (dr/current-route this this) first keyword)]
;;     (div :.ui.container
;;       (div :.ui.secondary.pointing.menu
;;         (dom/a :.item {:classes [(when (= :main current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["main"]))} "Main")
;;         (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
;;         (div :.right.menu
;;           (ui-login login)))
;;       (div :.ui.grid
;;         (div :.ui.row
;;           (ui-top-router router))))))

;; (def ui-top-chrome (comp/factory TopChrome))

;; ;;;;;;;;;;;;;;;;
;; ;; Bump Number
;; ;;;;;;;;;;;;;;;;

;; (defmutation bump-number [ignored]
;;   (action [{:keys [state]}]
;;           (swap! state update :root/number inc)))

;; (defsc BumpNumber [this {:root/keys [number]}]
;;        {:query         [:root/number]
;;         :initial-state {:root/number 0}}
;;        (dom/div
;;          (dom/h4 "This is an example.")
;;          (dom/button {:onClick #(comp/transact! this `[(bump-number {})])}
;;                      "You've clicked this button " number " times.")))

;; (def ui-bump-number (comp/factory BumpNumber))

;; ;;;;;;;;;;;;;;;;
;; ;; ROOT
;; ;;;;;;;;;;;;;;;;


;; (defsc Root [this {:root/keys [top-chrome]}]
;;   {:query             [{:root/top-chrome (comp/get-query TopChrome)}]
;;    :ident             (fn [] [:component/id :ROOT])
;;    :initial-state     {:root/top-chrome {}}}
;;   (div
;;     (ui-top-chrome top-chrome)
;;     (ui-bump-number {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; VANILLA EXAMPLES
;;;;;;;;;;;;;;;;




(defmethod m/mutate 'nav/settings [{:keys [state]} sym params]
           {:action (fn [] (swap! state assoc :panes [:settings :singleton]))})

(defmethod m/mutate 'nav/main [{:keys [state]} sym params]
           {:action (fn [] (swap! state assoc :panes [:main :singleton]))})

(defsc ItemLabel [this {:keys [value]}]
       {:initial-state (fn [{:keys [value]}] {:value value})
        :query         [:value]
        :ident         (fn [] [:labels/by-value value])}
       (dom/p value))

(def ui-label (comp/factory ItemLabel {:keyfn :value}))

;; Foo and Bar are elements of a mutli-type to-many union relation (each leaf can be a Foo or a Bar). We use params to
;; allow initial state to put more than one in place and have them be unique.
(defsc Foo [this {:keys [label]}]
       {:query         [:type :id {:label (comp/get-query ItemLabel)}]
        :initial-state (fn [{:keys [id label]}] {:id id :type :foo :label (comp/get-initial-state ItemLabel {:value label})})}
       (dom/div
         (dom/h2 "Foo")
         (ui-label label)))

(def ui-foo (comp/factory Foo {:keyfn :id}))

(defsc Bar [this {:keys [label]}]
       {:query         [:type :id {:label (comp/get-query ItemLabel)}]
        :initial-state (fn [{:keys [id label]}] {:id id :type :bar :label (comp/get-initial-state ItemLabel {:value label})})}
       (dom/div
         (dom/h2 "Bar")
         (ui-label label)))

(def ui-bar (comp/factory Bar {:keyfn :id}))

;; This is the to-many union component. It is the decision maker (it has no state or rendering of it's own)
;; The initial state of this component is the to-many (vector) value of various children
;; The render just determines which thing it is, and passes on the that renderer
(defsc ListItem [this {:keys [id type] :as props}]
       {:initial-state (fn [params] [(comp/get-initial-state Bar {:id 1 :label "A"}) (comp/get-initial-state Foo {:id 2 :label "B"}) (comp/get-initial-state Bar {:id 3 :label "C"})])
        :query         (fn [] {:foo (comp/get-query Foo) :bar (comp/get-query Bar)}) ; use lambda for unions
        :ident         (fn [] [type id])}                        ; lambda for unions
       (case type
             :foo (ui-foo props)
             :bar (ui-bar props)
             (dom/p "No Item renderer!")))

(def ui-list-item (comp/factory ListItem {:keyfn :id}))

;; Settings and Main are the target "Panes" of a to-one union (e.g. imagine tabs...we use buttons as the tab switching in
;; this example). The initial state looks very much like any other component, as does the rendering.
(defsc Settings [this {:keys [label]}]
       {:initial-state (fn [params] {:type :settings :id :singleton :label (comp/get-initial-state ItemLabel {:value "Settings"})})
        :query         [:type :id {:label (comp/get-query ItemLabel)}]}
       (ui-label label))

(def ui-settings (comp/factory Settings {:keyfn :type}))

(defsc Main [this {:keys [label]}]
       {:initial-state (fn [params] {:type :main :id :singleton :label (comp/get-initial-state ItemLabel {:value "Main"})})
        :query         [:type :id {:label (comp/get-query ItemLabel)}]}
       (ui-label label))

(def ui-main (comp/factory Main {:keyfn :type}))

;; This is a to-one union component. Again, it has no state of its own or rendering. The initial state is the single
;; child that should appear. Fulcro (during startup) will detect this component, and then use the query to figure out
;; what other children (the ones that have initial-state defined) should be placed into app state.
(defsc PaneSwitcher [this {:keys [id type] :as props}]
       {:initial-state (fn [params] (comp/get-initial-state Main nil))
        :query         (fn [] {:settings (comp/get-query Settings) :main (comp/get-query Main)})
        :ident         (fn [] [type id])}
       (case type
             :settings (ui-settings props)
             :main (ui-main props)
             (dom/p "NO PANE!")))

(def ui-panes (comp/factory PaneSwitcher {:keyfn :type}))

;; The root. Everything just composes to here (state and query)
;; Note, in core (where we create the app) there is no need to say anything about initial state!
(defsc Root [this {:keys [panes items]}]
       {:initial-state (fn [params] {:panes (comp/get-initial-state PaneSwitcher nil)
                                     :items (comp/get-initial-state ListItem nil)})
        :query         [{:items (comp/get-query ListItem)}
                        {:panes (comp/get-query PaneSwitcher)}]}
       (dom/div
         (dom/button {:onClick (fn [evt] (comp/transact! this '[(nav/settings)]))} "Go to settings")
         (dom/button {:onClick (fn [evt] (comp/transact! this '[(nav/main)]))} "Go to main")

         (ui-panes panes)

         (dom/h1 "Heterogenous list:")

         (dom/ul
           (mapv ui-list-item items))))


