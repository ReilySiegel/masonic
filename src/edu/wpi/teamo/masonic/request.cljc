(ns edu.wpi.teamo.masonic.request
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.map.node :as node]
            [edu.wpi.teamo.masonic.account :as account]
            [edu.wpi.teamo.masonic.client.ui.form :as form]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.dom :as dom]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [clojure.string :as str]
            [tick.alpha.api :as tick]
            [clojure.spec.alpha :as s])
  #?(:clj (:import
           (java.util Date)
           (java.time LocalDateTime Instant ZoneOffset)
           (edu.wpi.teamo.database.request BaseRequest ExtendedBaseRequest))))

(s/def ::due some?)

(def outputs [::id       
              {::locations [::node/id]}
              {::assigned [::account/username]} 
              ::complete?
              ::details  
              ::due      
              ::timestamp])

#?(:clj
   (defn request->m
     [^ExtendedBaseRequest obj]
     {::id        (.getID obj)
      ::locations
      (->> obj
           .getLocations
           .iterator
           iterator-seq
           (mapv (partial assoc {} ::node/id)))
      ::assigned  (mapv (partial assoc {} ::account/username) (str/split (.getAssigned obj) #","))
      ::complete? (.isComplete obj)
      ::details   (.getDetails obj)
      ::due       (.getDue obj)
      ::timestamp (.getTimestamp obj)}))

#?(:clj
   (defn ->BaseRequest [{::keys [id details locations assigned complete? due]}]
     (BaseRequest. id
                   details
                   (.stream (map ::node/id locations))
                   (str/join \, (map ::account/username assigned))
                   complete?
                   due)))


(comp/defsc FormAccountsQuery [_ _]
  {:query       [::account/username ::account/name fs/form-config-join]
   :ident       ::account/username
   :form-fields #{::account/username}})

(comp/defsc FormNodesQuery [_ _]
  {:query       [::node/id ::node/long-name fs/form-config-join]
   :ident       ::node/id
   :form-fields #{::node/id}})

(def form-query [::id ::details ::due ::complete? fs/form-config-join
                 {::locations (comp/get-query FormNodesQuery)}
                 {::assigned (comp/get-query FormAccountsQuery)}
                 {[::account/all '_] (comp/get-query FormAccountsQuery)}
                 {[::node/all '_] (comp/get-query FormNodesQuery)}])

(def form-fields #{::id ::assigned ::locations ::complete? ::details ::due})

(defn form-did-mount [this {accounts ::account/all
                            nodes    ::node/all}]
  (when-not (and (seq accounts) (seq nodes))
    (df/load! this ::account/all FormAccountsQuery)
    (df/load! this ::node/all FormNodesQuery)))

(defn form-elements [this]
  (let [{accounts ::account/all
         nodes    ::node/all}
        (comp/props this)]
    (comp/fragment
     (mui/grid {:item true :xs 12 :sm 6}
               (form/auto-complete this {::form/label    "Assign"
                                         ::form/field    ::assigned
                                         ::form/id-key   ::account/username
                                         ::form/options  accounts
                                         ::form/label-fn ::account/name}))
     (mui/grid {:item true :xs 12 :sm 6}
               (form/auto-complete this {::form/label   "Location"
                                         ::form/field   ::locations
                                         ::form/id-key  ::node/id
                                         ::form/options nodes
                                         ::form/label-fn
                                         (fn [node]
                                           (str (::node/long-name node) " (" (::node/id node) ")"))}))
     (mui/grid {:item true :xs 12 :sm 6}
               (form/date-time this {::form/field ::due ::form/label "Due"}))
     (mui/grid {:item true :xs 12 :sm 6}
               (form/checkbox this {::form/field ::complete? ::form/label "Complete"}))
     (mui/grid {:item true :xs 12}
               (form/text-field this {::form/label "Details" ::form/field ::details})))))

(def card-query [::id ::due ::complete?
                 {::assigned [:account/username ::account/name]}
                 {::locations [::node/id ::node/long-name]}])

(defn add-form* [state id fields]
  (let [ident   [::id id]
        request {::id        id
                 ::assigned  []
                 ::locations []
                 ::complete? false
                 ::due       nil
                 ::details   ""}]
    (-> state 
        (update-in ident merge (merge request fields)))))


(m/defmutation create [{::keys [form page-key form-key defaults]}]
  (action [{:keys [state]}]
          (let [id (str #?(:cljs (random-uuid)))]
            (swap! state
                   (fn [s]
                     (-> s
                         (add-form* id defaults)
                         (assoc-in [:ui/component page-key form-key] [::id id])
                         (assoc-in [:ui/component page-key :ui/open?] true)
                         (fs/add-form-config* form [::id id])))))))

(m/defmutation edit [{::keys [id form page-key form-key]}]
  (action [{:keys [state]}]
          (swap! state
                 (fn [s]
                   (-> s
                       (assoc-in [:ui/component page-key form-key] [::id id])
                       (assoc-in [:ui/component page-key :ui/open?] true)
                       (fs/add-form-config* form [::id id])
                       (fs/entity->pristine* [::id id])
                       (fs/mark-complete* [::id id]))))))


(defn card-elements [this]
  (let [{::keys [assigned locations due]} (comp/props this)]
    (comp/fragment
     (mui/typography {:noWrap true} (str/join ", " (map ::node/long-name locations)))
     (mui/typography {:noWrap true} (str/join ", " (map ::account/name assigned)))
     (when due (mui/typography {} (str "Due by: " (tick/format
                                                   (tick.format/formatter "LLL d, yyyy HH:mm")
                                                   due)))))))
