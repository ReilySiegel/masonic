(ns edu.wpi.teamo.masonic.client.ui.bar
  (:require [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.material :as m]))

(comp/defsc AppBar [this props]
  (m/app-bar
   {:position :static
    :color    :inherit}
   (m/toolbar
    {}
    (m/icon-button {:sx    {:mr 2
                            :p  1}
                    :edge  :start
                    :color :inherit}
                   (m/menu-icon {}))
    (m/typography {} "Masonic"))))

(def app-bar (comp/factory AppBar))
