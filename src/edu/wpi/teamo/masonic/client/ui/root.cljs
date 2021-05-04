(ns edu.wpi.teamo.masonic.client.ui.root
  (:require [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.bar :as bar]
            [edu.wpi.teamo.masonic.request :as request]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]
            [edu.wpi.teamo.masonic.client.ui.material :as m]))

(comp/defsc Root [this {::medicine/keys [page]}]
  {:query         [{::medicine/page (comp/get-query medicine/Page)}]
   :initial-state {::medicine/page {}}}
  (m/styles-provider
   {:injectFirst true}
   (m/theme-provider
    {:theme (m/create-mui-theme
             {:palette {:mode       :dark
                        :secondary  {:main         "#81A1C1"
                                     :contrastText "#ECEFF4"}
                        :primary    {:light        "#8FBCBB"
                                     :main         "#88C0D0"
                                     :dark         "#5E81AC"
                                     :contrastText "#ECEFF4"}
                        :error      {:main         "#BF616A"
                                     :contrastText "#ECEFF4"}
                        :warning    {:main         "#EBCB8B"
                                     :contrastText "#ECEFF4"}
                        :info       {:main         "#B48EAD"
                                     :contrastText "#ECEFF4"}
                        :success    {:main         "#A3BE8C"
                                     :contrastText "#ECEFF4"}
                        :text       {:primary   "#ECEFF4"
                                     :secondary "#E5E9F0"}
                        :background {:paper   "#3B4252"
                                     :default "#2E3440"}}})}
    (m/localization-provider
     {:dateAdapter m/adapter-date-fns}
     (comp/fragment
      (m/css-baseline)
      (bar/app-bar {})
      (m/container
       {:sx {:mt 2}}
       (medicine/page page)))))))
