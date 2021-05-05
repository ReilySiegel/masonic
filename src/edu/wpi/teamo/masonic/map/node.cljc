(ns edu.wpi.teamo.masonic.map.node
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.map :as map])
  #?(:clj (:import (edu.wpi.teamo.database.map NodeInfo Edge MapDB)
                   (edu.wpi.teamo.algos AlgoNode))))

#?(:clj (defn node-info->m [^NodeInfo obj]
          {::id         (.getNodeID obj)
           ::short-name (.getShortName obj)
           ::long-name  (.getLongName obj)
           ::type       (.getNodeType obj)
           ::building   (.getBuilding obj)
           ::floor      (.getFloor obj)
           ::x-pos      (.getXPos obj)
           ::y-pos      (.getYPos obj)}))

#?(:clj
   (pco/defresolver all [{::map/keys [db]}]
     {::pco/output [{::all [::id
                            ::short-name
                            ::long-name
                            ::type
                            ::building
                            ::floor
                            ::x-pos
                            ::y-pos]}]}
     {::all (mapv node-info->m
                  (iterator-seq (.iterator (.getAllNodes db))))}))

#?(:clj
   (pco/defresolver by-id [items]
     {::pco/input  [::id]
      ::pco/output [::short-name
                    ::long-name
                    ::type
                    ::building
                    ::floor
                    ::x-pos
                    ::y-pos]
      ::pco/batch? true}
     (let [idx (into {} (map (juxt ::id identity)) (::all (all {::map/db map/mdb})))]
       (into []
             (comp
              (map ::id)
              (map (partial get idx)))
             items))))
