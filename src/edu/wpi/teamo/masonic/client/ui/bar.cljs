(ns edu.wpi.teamo.masonic.client.ui.bar
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [edu.wpi.teamo.masonic.client.ui.router :as router]
            [clojure.string :as str]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]))
(defn routes []
  (->> router/Router
       comp/component-options
       :router-targets
       (map comp/component-options)
       (map (juxt :label :icon :route-segment))
       (remove (partial every? nil?))
       (map (partial zipmap [::label ::icon ::path]))))

(comp/defsc AppBar [this {:ui/keys [open?]}]
  {:query         [:ui/open?]
   :ident         (fn [] [:ui/component ::bar])
   :initial-state {:ui/open? false}}
  (mui/app-bar
   {:position :static
    :color    :inherit}
   (comp/fragment
    (mui/toolbar
     {}
     (mui/icon-button {:sx      {:mr 2
                                 :p  1}
                       :edge    :start
                       :color   :inherit
                       :onClick #(m/toggle! this :ui/open?)}
                      (mui/menu-icon {}))
     (mui/typography {} "Masonic"))
    (mui/drawer
     {:open    open?
      :onClose #(m/toggle! this :ui/open?)}
     (mui/list
      {}
      (for [{::keys [label icon path]} (routes)]
        (mui/list-item
         {:button  true
          :key     (str/join \/ path)
          :onClick (fn [_]
                     (dr/change-route! this path)
                     (m/toggle!! this :ui/open?))}
         (mui/list-item-icon {} icon)
         (mui/list-item-text {:primary label}))))))))

(def app-bar (comp/factory AppBar))
