(ns edu.wpi.teamo.masonic.client.ui.root
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.ui-state-machines :as uism]
            [edu.wpi.teamo.masonic.client.ui.bar :as bar]
            [edu.wpi.teamo.masonic.client.ui.material :as m]
            [edu.wpi.teamo.masonic.client.ui.router :as router]
            [edu.wpi.teamo.masonic.request.medicine :as medicine]))

(defn make-theme [mode]
  {:palette (merge {:secondary {:main         "#81A1C1"
                                :contrastText "#2E3440"}
                    :primary   {:light        "#8FBCBB"
                                :main         "#88C0D0"
                                :dark         "#5E81AC"
                                :contrastText "#2E3440"}
                    :error     {:main         "#BF616A"
                                :contrastText "#2E3440"}
                    :warning   {:main         "#EBCB8B"
                                :contrastText "#2E3440"}
                    :info      {:main         "#B48EAD"
                                :contrastText "#2E3440"}
                    :success   {:main         "#A3BE8C"
                                :contrastText "#2E3440"}}
                   (if (= :light mode)
                     {:text       {:secondary "#3B4252"
                                   :primary   "#2E3440"}
                      :background {:default "#ECEFF4"
                                   :paper   "#E5E9F0"}}
                     {:text       {:primary   "#ECEFF4"
                                   :secondary "#E5E9F0"}
                      :background {:paper   "#3B4252"
                                   :default "#2E3440"}})
                   {:mode mode})
   :components 
   {:MuiIconButton
    {:styleOverrides
     {:root {:transition "color 1000ms"}}}
    :MuiTypography
    {:styleOverrides
     {:root {:transition "color 1000ms"}}}
    :MuiPaper
    {:styleOverrides
     {:root {:transition "background-color 1000ms"}}}
    :MuiCssBaseline
    {:styleOverrides
     {:body (merge {:transition "background-color 1000ms"}
                   (js->clj
                    (m/dark-scrollbar (if (= :light mode)
                                        {:track  "#E5E9F0"
                                         :thumb  "#D8DEE9"
                                         :active "#D8DEE9"}
                                        {:track  "#3B4252"
                                         :thumb  "#4C566A"
                                         :active "#4C566A"}))))}}}})

(comp/defsc Root [this {::bar/keys      [bar]
                        ::medicine/keys [page]
                        ::router/keys   [router]
                        :ui/keys        [theme]}]
  {:query         [{::bar/bar (comp/get-query bar/AppBar)}
                   {::router/router (comp/get-query router/Router)}
                   {::medicine/page (comp/get-query medicine/Page)}
                   [::uism/asm-id ::router/Router]
                   :ui/theme]
   :initial-state {::bar/bar       {}
                   ::medicine/page {}
                   ::router/router {}
                   :ui/theme       :dark}}
  (m/styles-provider
   {:injectFirst true}
   (m/theme-provider
    {:theme (m/create-mui-theme (make-theme theme))}
    (let [top-router-state (or (uism/get-active-state this ::router/Router) :initial)]
      (m/localization-provider
       {:dateAdapter m/adapter-date-fns}
       (comp/fragment
        (m/css-baseline)
        (bar/app-bar bar)
        (m/container
         {:sx {:mt 2
               :mb 2}}
         (when (= :routed top-router-state)
           (router/router router)))))))))
