(ns edu.wpi.teamo.masonic.request.gift
  (:require [com.wsscode.pathom3.connect.operation :as pco]
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
  #?(:clj (:import edu.wpi.teamo.database.request.GiftRequest)))

(def defaults {::recipient   ""
               ::tracking-id ""})

(def outputs (keys defaults))


#?(:clj
   (defn request->m [^GiftRequest obj]
     (merge
      (request/request->m obj)
      {::recipient   (.getRecipient obj)
       ::tracking-id (.getTrackingID obj)})))

#?(:clj
   (pco/defresolver all []
     {::pco/output [{::all (into request/outputs outputs)}]}
     {::all (->> (GiftRequest/getAll)
                 (.iterator)
                 iterator-seq
                 (mapv request->m))}))

#?(:clj
   (pco/defmutation upsert [{::keys         [recipient tracking-id]
                             ::request/keys [id]
                             :as            req}]
     (.update (GiftRequest. recipient tracking-id (request/->BaseRequest req)))
     {::request/id id})
   :cljs
   (m/defmutation upsert [{::request/keys [id]}]
     (action [{:keys [state]}]
             (swap! state (fn [s]
                            (-> s
                                (dt/integrate-ident* [::request/id id]
                                                     :prepend [:ui/component ::page ::all])))))
     (remote [_] true)))

(comp/defsc Form [this props]
  {:query             (fn [] (into request/form-query outputs))
   :ident             ::request/id
   :form-fields       (into request/form-fields outputs)
   :componentDidMount request/form-did-mount}
  (comp/fragment
   (mui/grid {:xs 12 :sm 6 :item true}
             (form/text-field this {::form/label "Recipient" ::form/field ::recipient}))
   (mui/grid {:xs 12 :sm 6 :item true}
             (form/text-field this {::form/label "Tracking ID" ::form/field ::tracking-id}))
   (request/form-elements this)))

(def form (comp/factory Form))

(comp/defsc Card [this {::keys         [recipient tracking-id]
                        ::request/keys [id]}]
  {:query (fn [] (into request/card-query outputs))
   :ident ::request/id}
  (mui/grid
   {:item true :xs 12 :sm 4}
   (mui/card
    {:onClick #(comp/transact! this [(request/edit {::request/id       id
                                                    ::request/form     Form
                                                    ::request/form-key ::formp
                                                    ::request/page-key ::page})])}
    (mui/card-action-area
     {}
     (mui/card-content
      {}
      (when recipient (mui/typography {}  (str "For: " recipient)))
      (request/card-elements this)
      (mui/typography {} tracking-id))))))

(def card (comp/factory Card {:keyfn ::request/id}))

(comp/defsc Page [this {::keys   [formp all]
                        :ui/keys [open?]}]
  {:query         [:ui/open?
                   {::formp (comp/get-query Form)}
                   {::all (comp/get-query Card)}]
   :ident         (fn [] [:ui/component ::page])
   :initial-state {:ui/open? false}
   :route-segment ["request" "gift"]
   :label         "Gift Request"
   :icon          (mui/redeem-icon {})
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
                                                              ::request/form-key ::formp
                                                              ::request/page-key ::page
                                                              ::request/defaults defaults})])}
            (mui/add-icon {}))
   (mui/dialog
    {:open      open?
     :fullWidth true
     :maxWidth  :md
     :onClose   #(comp/transact! this [(fs/reset-form! {:form-ident [::request/id
                                                                     (::request/id formp)]})
                                       `(m/toggle {:field :ui/open?})])}
    (mui/dialog-content
     {}
     (mui/grid
      {:container true
       :spacing   2}
      (form formp)))
    (form/dialog-actions this Form formp upsert))))

(def page (comp/factory Page))
