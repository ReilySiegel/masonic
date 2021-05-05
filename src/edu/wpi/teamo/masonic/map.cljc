(ns edu.wpi.teamo.masonic.map
  (:require [com.wsscode.pathom3.connect.operation :as pco])
  #?(:clj (:import (edu.wpi.teamo.database.map MapDB))))

#?(:clj (defonce ^MapDB mdb (MapDB.)))

#?(:clj (pco/defresolver db []
          {::db mdb}))
