(ns edu.wpi.teamo.masonic.map.edge
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.map :as map]
            [edu.wpi.teamo.masonic.map.node :as node])
  (:import edu.wpi.teamo.database.map.EdgeInfo))

(defn edge-info->m [^EdgeInfo obj]
  {::id    (.getEdgeID obj)
   ::start {::node/id (.getStartNodeID obj)}
   ::end   {::node/id (.getEndNodeID obj)}})

(pco/defresolver all [{::map/keys [db]}]
  {::pco/output [{::all [::id
                         {::start [::node/id]}
                         {::end [::node/id]}]}]}
  {::all (mapv edge-info->m
               (iterator-seq (.iterator (.getAllEdges db))))})

(pco/defresolver by-id [items]
  {::pco/input  [::id]
   ::pco/output [{::start [::node/id]}
                 {::end [::node/id]}]
   ::pco/batch? true}
  (let [idx (into {} (map (juxt ::id identity)) (::all (all {::map/db map/mdb})))]
    (into []
          (comp
           (map ::id)
           (map (partial get idx)))
          items)))
