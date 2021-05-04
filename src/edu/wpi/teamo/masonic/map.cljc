(ns edu.wpi.teamo.masonic.map
  (:require [com.wsscode.pathom3.connect.operation :as pco])
  #?(:clj (:import (edu.wpi.teamo.database.map MapDB)
                   (edu.wpi.teamo.algos AStarManager))))

#?(:clj (defonce ^MapDB mdb (MapDB.)))

#?(:clj (defonce ^AStarManager a* (AStarManager. mdb)))

#?(:clj (pco/defresolver db []
          {::db mdb}))
