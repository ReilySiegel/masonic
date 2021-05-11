(ns edu.wpi.teamo.masonic.client.ui.router
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [edu.wpi.teamo.masonic.client.ui.home :as home]
            [edu.wpi.teamo.masonic.request.food :as food]
            [edu.wpi.teamo.masonic.request.gift :as gift]
            [edu.wpi.teamo.masonic.request.laundry :as laundry]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]
            [edu.wpi.teamo.masonic.request.sanitation :as sanitation]))

(dr/defrouter Router [this props]
  {:router-targets [home/Page food/Page gift/Page laundry/Page medicine/Page sanitation/Page]})

(def router (comp/factory Router))
