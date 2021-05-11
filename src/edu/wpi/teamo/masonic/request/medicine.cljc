(ns edu.wpi.teamo.masonic.request.medicine
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.request.drugs :as drugs]
            [edu.wpi.teamo.masonic.request :as request]
            [edu.wpi.teamo.masonic.account :as account]
            [edu.wpi.teamo.masonic.map.node :as node]
            [edu.wpi.teamo.masonic.client.ui.form :as form]
            [edu.wpi.teamo.masonic.specs :as specs]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as dt]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [clojure.string :as str]
            [clojure.spec.alpha :as s])
  #?(:clj
     (:import edu.wpi.teamo.database.request.MedicineRequest)))

(s/def ::type ::specs/non-empty-string)
(s/def ::amount ::specs/non-empty-string)

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

(comp/defsc Form [this {::keys [amount type]}]
  {:query             (fn [] (into request/form-query
                                   [::amount ::type]))
   :ident             ::request/id
   :form-fields       (into request/form-fields #{::amount ::type})
   :componentDidMount request/form-did-mount}
  (comp/fragment
   (mui/grid {:item true
              :xs   12
              :sm   6}
             (form/auto-complete this {::form/label   "Type"
                                       ::form/field   ::type
                                       ::form/options drugs/list}))
   (mui/grid {:item true
              :xs   12
              :sm   6}
             (form/text-field this {::form/field ::amount
                                    ::form/label "Amount"}))
   (request/form-elements this)))

(def form (comp/factory Form))

(comp/defsc Card [this {::keys         [type amount]
                        ::request/keys [id]}]
  {:query (fn [] (into request/card-query [::type ::amount]))
   :ident ::request/id}
  (mui/grid
   {:item true :xs 12 :sm 4}
   (mui/card
    {:onClick #(comp/transact! this [(request/edit {::request/id       id
                                                    ::request/form     Form
                                                    ::request/form-key ::form
                                                    ::request/page-key ::page})])}
    (mui/card-action-area
     {}
     (mui/card-content
      {}
      (mui/typography {:variant :h6 :noWrap true} (str type " - " amount))
      (request/card-elements this))))))

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
             :sx      {:position :fixed
                       :bottom   32
                       :right    32}
             :onClick #(comp/transact! this [(request/create {::request/form     Form
                                                              ::request/form-key ::form
                                                              ::request/page-key ::page
                                                              ::request/defaults
                                                              {::type   ""
                                                               ::amount ""}})])}
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
    (form/dialog-actions this Form form upsert))))

(def page (comp/factory Page))
