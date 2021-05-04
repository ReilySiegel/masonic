(ns edu.wpi.teamo.masonic.request.medicine
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.request :as request]
            [edu.wpi.teamo.masonic.account :as account]
            [edu.wpi.teamo.masonic.map.node :as node]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as dt]
            [clojure.string :as str])
  #?(:clj
     (:import edu.wpi.teamo.database.request.MedicineRequest)))

#?(:clj
   (defn medicine-request->m [^MedicineRequest obj]
     (merge
      (request/request->m obj)
      {::type   (.getType obj)
       ::amount (.getAmount obj)})))

#?(:clj
   (pco/defresolver all []
     {::pco/output [{::all (into request/outputs [::type
                                                  ::amount])}]}
     {::all (->> (MedicineRequest/getAll)
                 (.iterator)
                 iterator-seq
                 (mapv medicine-request->m))}))

(comp/defsc Form [this {::keys         [amount type]
                        ::request/keys [assigned locations complete? details due]
                        accounts       ::account/all
                        nodes          ::node/all}]
  {:query             [::amount ::type
                       ::request/id ::request/assigned ::request/locations ::request/complete?
                       ::request/details ::request/due
                       fs/form-config-join
                       {[::account/all '_] (comp/get-query request/FormAccountsQuery)}
                       {[::node/all '_] (comp/get-query request/FormNodesQuery)}]
   :ident             ::request/id
   :form-fields       #{::request/assigned ::request/locations ::request/complete? ::request/details
                        ::request/due ::amount ::type}
   :initial-state     {}
   :componentDidMount (fn [this {accounts ::account/all
                                 nodes    ::node/all}]
                        (when-not (and (seq accounts) (seq nodes))
                          (df/load! this ::account/all request/FormAccountsQuery)
                          (df/load! this ::node/all request/FormNodesQuery)))}
  (comp/fragment
   (mui/grid {:item true
              :xs   12
              :sm   6}
             (mui/text-field {:label     "Type"
                              :fullWidth true
                              :value     type
                              :onChange  #(m/set-string! this ::type :event %)}))
   (mui/grid {:item true
              :xs   12
              :sm   6}
             (mui/text-field {:label     "Amount"
                              :fullWidth true
                              :value     amount
                              :onChange  #(m/set-string! this ::amount :event %)}))
   (mui/grid {:item true :xs 12 :sm 6}
             (mui/auto-complete
              {:renderInput          #(mui/text-field
                                       (mui/merge* % {:label "Assigned"}))
               :multiple             true
               :value                (map second assigned)
               :onChange             (fn [_ v]
                                       (m/set-value! this ::request/assigned
                                                     (mapv (partial conj [::account/username])
                                                           (mui/->clj v))))
               :disableCloseOnSelect true
               :getOptionLabel       (fn [id]
                                       (::account/name
                                        (first (filter (comp #{id} ::account/username) accounts))))
               :options              (or (map ::account/username accounts) [])}))
   (mui/grid {:item true :xs 12 :sm 6}
             (mui/auto-complete
              {:renderInput          #(mui/text-field
                                       (mui/merge* % {:label "Location"}))
               :multiple             true
               :value                (map second locations)
               :onChange             (fn [_ v]
                                       (m/set-value! this
                                                     ::request/locations
                                                     (mapv (partial conj [::node/id])
                                                           (mui/->clj v))))
               :disableCloseOnSelect true
               :getOptionLabel       (fn [id]
                                       (let [node (first (filter (comp #{id} ::node/id) nodes))]
                                         (str (::node/long-name node) " (" (::node/id node) ")")))
               :options              (or  (map ::node/id nodes) [])}))
   (mui/grid {:item true :xs 12 :sm 6}
             (mui/date-time-picker {:label       "Due"
                                    :renderInput #(mui/text-field (mui/merge* % {:fullWidth true}))
                                    :ampm        false
                                    :value       due
                                    :onChange    #(m/set-value! this ::request/due %)}))
   (mui/grid {:item true :xs 12 :sm 6}
             (mui/form-control-label
              {:label   "Complete"
               :control (mui/checkbox {:checked  complete?
                                       :onChange #(m/set-value! this
                                                                ::request/complete?
                                                                (.. % -target -checked))})}))
   (mui/grid {:item true :xs 12}
             (mui/text-field {:label     "Details"
                              :fullWidth true
                              :multiline true
                              :value     details
                              :onChange  #(m/set-string! this ::request/details :event %)}))))

(def form (comp/factory Form))

(defn add-form* [state id]
  (let [ident   [::request/id id]
        request {::request/id        id
                 ::request/assigned  []
                 ::request/locations []
                 ::request/complete? false
                 ::request/due       nil
                 ::request/details   ""
                 ::type              ""
                 ::amount            ""}]
    (-> state 
        (update-in ident merge request))))


(m/defmutation create [_]
  (action [{:keys [state]}]
          (let [id (str #?(:cljs (random-uuid)))]
            (swap! state
                   (fn [s]
                     (-> s
                         (add-form* id)
                         (assoc-in [:ui/component ::page ::form] [::request/id id])
                         (assoc-in [:ui/component ::page :ui/open?] true)
                         (fs/add-form-config* Form [::request/id id])))))))

(m/defmutation edit [{::request/keys [id]}]
  (action [{:keys [state]}]
          (swap! state
                 (fn [s]
                   (-> s
                       (assoc-in [:ui/component ::page ::form] [::request/id id])
                       (assoc-in [:ui/component ::page :ui/open?] true)
                       (fs/add-form-config* Form [::request/id id])
                       (fs/entity->pristine* [::request/id id]))))))


(m/defmutation submit [{::request/keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (dt/integrate-ident* [::request/id id]
                                                  :prepend [:ui/component ::page ::all]))))))

(comp/defsc Card [this {::keys         [type amount]
                        ::request/keys [id locations assigned due]}]
  {:query [::type ::amount
           ::request/id ::request/due ::request/complete?
           {::request/assigned [:account/username ::account/name]}
           {::request/locations [::node/id ::node/long-name]}]
   :ident ::request/id}
  (mui/grid
   {:item true :xs 12 :sm 4}
   (mui/card
    {:onClick #(comp/transact! this [(edit {::request/id id})])}
    (mui/card-content
     {}
     (mui/typography {:variant :h5} (str type " - " amount))
     (mui/typography {:noWrap true} (str/join ", " (map ::account/name assigned)))
     (mui/typography {:noWrap true} (str/join ", " (map ::node/long-name locations)))
     (when due (mui/typography {} (str "Due by: " (.toLocaleString due
                                                                   "en-US"
                                                                   #js {:dateStyle "long"
                                                                        :timeStyle "short"
                                                                        :hour12    false}))))))))

(def card (comp/factory Card {:keyfn ::request/id}))

(comp/defsc Page [this {::keys   [form all]
                        :ui/keys [open?]}]
  {:query         [:ui/open?
                   {::form (comp/get-query Form)}
                   {::all (comp/get-query Card)}]
   :ident         (fn [] [:ui/component ::page])
   :initial-state {:ui/open? false}}
  (comp/fragment
   (mui/grid
    {:container true :spacing 4}
    (map card (sort-by (juxt ::request/complete? ::request/due) all)))
   (mui/fab {:color   :primary
             :sx      {:position :absolute
                       :bottom   32
                       :right    32}
             :onClick #(comp/transact! this [(create)])}
            (mui/add-icon {}))
   (mui/dialog
    {:open      open?
     :fullWidth true
     :maxWidth  :md
     :onClose   #(m/toggle! this :ui/open?)}
    (mui/dialog-content
     {}
     (mui/grid
      {:container true
       :spacing   2}
      (edu.wpi.teamo.masonic.request.medicine/form form)))
    (mui/dialog-actions
     {}
     (mui/button {:onClick #(comp/transact! this [(fs/reset-form! {:form-ident [::request/id
                                                                                (::request/id form)]})
                                                  (m/toggle {:field :ui/open?})])} "Cancel")
     (mui/button {:onClick #(comp/transact! this [(submit form)
                                                  (m/toggle {:field :ui/open?})])} "Submit")))))

(def page (comp/factory Page))
