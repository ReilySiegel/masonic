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
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [clojure.string :as str]
            [tick.alpha.api :as tick]
            [clojure.spec.alpha :as s])
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

(comp/defsc Form [this {::keys [amount type]}]
  {:query             (fn [] (into request/form-query
                                   [::amount ::type]))
   :ident             ::request/id
   :form-fields       (into request/form-fields #{::amount ::type})
   :initial-state     (fn [_] {})
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
   (request/form-elements this)))

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
                       (fs/entity->pristine* [::request/id id])
                       (fs/mark-complete* [::request/id id]))))))


#?(:clj
   (pco/defmutation upsert [{::keys         [amount type]
                             ::request/keys [id]
                             :as            req}]
     (.update (MedicineRequest. type amount (request/->BaseRequest req)))
     {::request/id id})
   :cljs
   (m/defmutation upsert [{::request/keys [id]}]
     (action [{:keys [state]}]
             (swap! state (fn [s]
                            (-> s
                                (dt/integrate-ident* [::request/id id]
                                                     :prepend [:ui/component ::page ::all])))))
     (remote [_] true)))

(comp/defsc Card [this {::keys         [type amount]
                        ::request/keys [id]}]
  {:query (fn [] (into request/card-query [::type ::amount]))
   :ident ::request/id}
  (mui/grid
   {:item true :xs 12 :sm 4}
   (mui/card
    {:onClick #(comp/transact! this [(edit {::request/id id})])}
    (mui/card-content
     {}
     (mui/typography {:variant :h5} (str type " - " amount))
     (request/card-elements this)))))

(def card (comp/factory Card {:keyfn ::request/id}))

(comp/defsc Page [this {::keys   [form all]
                        :ui/keys [open?]}]
  {:query         [:ui/open?
                   {::form (comp/get-query Form)}
                   {::all (comp/get-query Card)}]
   :ident         (fn [] [:ui/component ::page])
   :initial-state {:ui/open? false}
   :route-segment ["request" "medicine"]
   :label         "Medicine Request"
   :icon          (mui/local-pharmacy-icon {})
   :will-enter    (fn [app _]
                    (dr/route-deferred
                     [:ui/component ::page]
                     #(df/load! app ::all Form
                                {:post-mutation        `dr/target-ready
                                 :post-mutation-params {:target [:ui/component ::page]}
                                 :target               [:ui/component ::page ::all]})))}
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
     :onClose   #(comp/transact! this [(fs/reset-form! {:form-ident [::request/id
                                                                     (::request/id form)]})
                                       `(m/toggle {:field :ui/open?})])}
    (mui/dialog-content
     {}
     (mui/grid
      {:container true
       :spacing   2}
      (edu.wpi.teamo.masonic.request.medicine/form form)))
    (mui/dialog-actions
     {}
     (mui/button {:disabled (not (fs/dirty? form))
                  :onClick  #(comp/transact! this [(fs/reset-form! {:form-ident [::request/id
                                                                                 (::request/id form)]})
                                                   `(m/toggle {:field :ui/open?})])} "Cancel")
     (mui/button {:disabled (not= :valid (fs/get-spec-validity form ::request/due))
                  :onClick  #(comp/transact! this [(upsert form)
                                                   `(m/toggle {:field :ui/open?})])} "Submit")))))

(def page (comp/factory Page))
