(ns edu.wpi.teamo.masonic.client.ui.root
  (:require [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.bar :as bar]
            [edu.wpi.teamo.masonic.request :as request]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]
            [edu.wpi.teamo.masonic.client.ui.material :as m]
            [edu.wpi.teamo.masonic.client.ui.router :as router]))

(comp/defsc Root [this {::bar/keys      [bar]
                        ::medicine/keys [page]
                        ::router/keys   [router]}]
  {:query         [{::bar/bar (comp/get-query bar/AppBar)}
                   {::router/router (comp/get-query router/Router)}
                   {::medicine/page (comp/get-query medicine/Page)}]
   :initial-state {::bar/bar       {}
                   ::medicine/page {}
                   ::router/router {}}}
  (m/styles-provider
   {:injectFirst true}
   (m/theme-provider
    {:theme (m/create-mui-theme
             {:palette {:mode       :light
                        :secondary  {:main         "#81A1C1"
                                     :contrastText "#2E3440"}
                        :primary    {:light        "#8FBCBB"
                                     :main         "#88C0D0"
                                     :dark         "#5E81AC"
                                     :contrastText "#2E3440"}
                        :error      {:main         "#BF616A"
                                     :contrastText "#2E3440"}
                        :warning    {:main         "#EBCB8B"
                                     :contrastText "#2E3440"}
                        :info       {:main         "#B48EAD"
                                     :contrastText "#2E3440"}
                        :success    {:main         "#A3BE8C"
                                     :contrastText "#2E3440"}
                        :text       {:secondary "#3B4252"
                                     :primary   "#2E3440"}
                        :background {:default "#ECEFF4"
                                     :paper   "#E5E9F0"}}})}
    (m/localization-provider
     {:dateAdapter m/adapter-date-fns}
     (comp/fragment
      (m/css-baseline)
      (bar/app-bar bar)
      (m/container
       {:sx {:mt 2}}
       (router/router router)))))))
