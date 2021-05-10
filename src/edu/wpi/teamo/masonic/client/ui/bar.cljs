(ns edu.wpi.teamo.masonic.client.ui.bar
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [edu.wpi.teamo.masonic.client.ui.router :as router]
            [edu.wpi.teamo.masonic.client.routing :as routing]
            [clojure.string :as str]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.application :as app]))

(defn routes []
  (->> router/Router
       comp/component-options
       :router-targets
       (map comp/component-options)
       (map (juxt :label :icon :route-segment))
       (remove (partial every? nil?))
       (map (partial zipmap [::label ::icon ::path]))))

(comp/defsc Busy [_this {::app/keys [active-remotes]}]
  {:query         [[::app/active-remotes '_]]
   :ident         (fn [] [:ui/component ::busy])
   :initial-state {}}
  (let [busy? (boolean (seq active-remotes))]
    (mui/box
     {:sx {:display (if busy? :block :none)}}
     (mui/linear-progress {:variant (if busy?
                                      :indeterminate
                                      :determinate)
                           :value   100
                           :color   :secondary}))))

(def ui-busy (comp/factory Busy))


(comp/defsc AppBar [this {:ui/keys [open?]
                          ::keys   [busy]}]
  {:query         [:ui/open?
                   {::busy (comp/get-query Busy)}]
   :ident         (fn [] [:ui/component ::bar])
   :initial-state {:ui/open? false
                   ::busy    {}}}
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
    (ui-busy busy)
    (mui/drawer
     {:open    open?
      :onClose #(m/toggle!! this :ui/open?)}
     (mui/list
      {}
      (for [{::keys [label icon path]} (routes)]
        (mui/list-item
         {:button  true
          :key     (str/join \/ path)
          :onClick #(comp/transact!! this
                                     [(routing/route-to {:route path})
                                      (m/toggle {:field :ui/open?})])}
         (mui/list-item-icon {} icon)
         (mui/list-item-text {:primary label}))))))))

(def app-bar (comp/factory AppBar))
