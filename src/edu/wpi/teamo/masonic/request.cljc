(ns edu.wpi.teamo.masonic.request
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.map.node :as node]
            [edu.wpi.teamo.masonic.account :as account]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.dom :as dom]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [clojure.string :as str]
            [tick.alpha.api :as tick])
  #?(:clj (:import
           (java.util Date)
           (java.time LocalDateTime Instant ZoneOffset)
           (edu.wpi.teamo.database.request BaseRequest ExtendedBaseRequest))))

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
  {:query [::account/username ::account/name]
   :ident ::account/username})

(comp/defsc FormNodesQuery [_ _]
  {:query [::node/id ::node/long-name]
   :ident ::node/id})

(def form-query [::id ::details ::due ::complete? fs/form-config-join
                 {::locations [::node/id ::node/long-name]}
                 {::assigned [::account/username ::account/name]}
                 {[::account/all '_] (comp/get-query FormAccountsQuery)}
                 {[::node/all '_] (comp/get-query FormNodesQuery)}])

(def form-fields #{::assigned ::locations ::complete? ::details ::due})

(defn form-elements [this]
  (let [{::keys   [assigned locations due complete? details]
         accounts ::account/all
         nodes    ::node/all}
        (comp/props this)]
    (comp/fragment
     (mui/grid {:item true :xs 12 :sm 6}
               (mui/auto-complete
                {:renderInput          #(mui/text-field
                                         (mui/merge* % {:label "Assigned"}))
                 :multiple             true
                 :value                (map ::account/username assigned)
                 :onChange             (fn [_ v]
                                         (m/set-value! this ::assigned
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
                 :value                (map ::node/id locations)
                 :onChange             (fn [_ v]
                                         (m/set-value! this
                                                       ::locations
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
                                      :onChange    #(m/set-value! this ::due
                                                                  (try (tick/date-time %)
                                                                       (catch :default e
                                                                         nil)))}))
     (mui/grid {:item true :xs 12 :sm 6}
               (mui/form-control-label
                {:label   "Complete"
                 :control (mui/checkbox {:checked  complete?
                                         :onChange #(m/set-value! this
                                                                  ::complete?
                                                                  (.. % -target -checked))})}))
     (mui/grid {:item true :xs 12}
               (mui/text-field {:label     "Details"
                                :fullWidth true
                                :multiline true
                                :value     details
                                :onChange  #(m/set-string! this ::details :event %)})))))

(def card-query [::id ::due ::complete?
                 {::assigned [:account/username ::account/name]}
                 {::locations [::node/id ::node/long-name]}])

(defn card-elements [this]
  (let [{::keys [assigned locations due]} (comp/props this)]
    (comp/fragment
     (mui/typography {:noWrap true} (str/join ", " (map ::account/name assigned)))
     (mui/typography {:noWrap true} (str/join ", " (map ::node/long-name locations)))
     (when due (mui/typography {} (str "Due by: " (tick/format
                                                   (tick.format/formatter "LLL d, yyyy HH:mm")
                                                   due)))))))
