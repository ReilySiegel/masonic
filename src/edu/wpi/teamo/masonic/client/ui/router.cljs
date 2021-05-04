(ns edu.wpi.teamo.masonic.client.ui.router
  (:require [com.fulcrologic.fulcro.dynamic-router :as dr]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]))

(dr/defrouter [this props]
  {:router-targets [medicine/Page]})
