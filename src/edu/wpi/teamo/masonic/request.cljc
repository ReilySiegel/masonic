(ns edu.wpi.teamo.masonic.request
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.map.node :as node]
            [edu.wpi.teamo.masonic.account :as account]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.dom :as dom]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs])
  #?(:clj (:import (edu.wpi.teamo.database.request ExtendedBaseRequest))))

(def outputs [::id       
              {::locations [::node/id]}
              ::assigned 
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
      ::assigned  (.getAssigned obj)
      ::complete? (.isComplete obj)
      ::details   (.getDetails obj)
      ::due       (str (.getDue obj))
      ::timestamp (str (.getTimestamp obj))}))


(comp/defsc FormAccountsQuery [_ _]
  {:query [::account/username ::account/name]
   :ident ::account/username})

(comp/defsc FormNodesQuery [_ _]
  {:query [::node/id ::node/long-name]
   :ident ::node/id})
