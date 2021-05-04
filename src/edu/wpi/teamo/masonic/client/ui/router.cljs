(ns edu.wpi.teamo.masonic.client.ui.router
  (:require [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]
            [edu.wpi.teamo.masonic.client.ui.home :as home]
            [com.fulcrologic.fulcro.components :as comp]))

(dr/defrouter Router [this props]
  {:router-targets [home/Page medicine/Page]})

(def router (comp/factory Router))
