(ns edu.wpi.teamo.masonic.client.ui.home
  (:require [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]))

(comp/defsc Page [this _]
  {:query         []
   :ident         (fn [] [:ui/component ::page])
   :initial-state {}
   :route-segment [""]
   :label         "Home"
   :icon          (mui/dashboard-icon {})}
  (mui/typography {} "Home"))
