(ns edu.wpi.teamo.masonic.client.ui.home
  (:require [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [clojure.string :as str]))

(def fs [{::name    "Clojure"
          ::creator "Cognitect"
          ::url     "https://clojure.org"}
         {::name    "ClojureScript"
          ::creator "Cognitect"
          ::url     "https://clojurescript.org"}
         {::name    "Shadow CLJS"
          ::creator "Thomas Heller"
          ::url     "https://github.com/thheller/shadow-cljs"}
         {::name    "Fulcro"
          ::creator "Tony Kay"
          ::url     "https://fulcro.fulcrologic.com"}
         {::name    "Pathom3"
          ::creator "Wilker Lucio"
          ::url     "https://pathom3.wsscode.com"}
         {::name    "Material UI"
          ::creator "Material UI"
          ::url     "https://material-ui.com/"}
         {::name    "Ring"
          ::creator "James Reeves"
          ::url     "https://github.com/ring-clojure/ring"}
         {::name    "HTTP-Kit"
          ::creator "Peter Taoussanis"
          ::url     "https://github.com/http-kit/http-kit"}
         {::name    "Integrant"
          ::creator "James Reeves"
          ::url     "https://github.com/weavejester/integrant"}
         {::name    "Hiccup"
          ::creator "James Reeves"
          ::url     "https://github.com/weavejester/hiccup"}
         {::name    "Tick"
          ::creator "JUXT"
          ::url     "https://github.com/juxt/tick"}
         {::name    "Pushy"
          ::creator "CLJ Commons"
          ::url     "https://github.com/clj-commons/pushy"}])

(defn fs-card [{::keys [name creator url]}]
  (mui/grid
   {:item true :xs 12 :sm 4 :key url}
   (mui/card
    {}
    (mui/card-action-area
     {:onClick #(js/open url "_blank")}
     (mui/card-content
      {}
      (mui/typography {:variant :h6} name)
      (mui/typography {:variant :subtitle} creator))))))

(comp/defsc Page [this _]
  {:query         []
   :ident         (fn [] [:ui/component ::page])
   :initial-state {}
   :route-segment [""]
   :label         "Home"
   :icon          (mui/dashboard-icon {})}
  (comp/fragment
   (mui/typography {:variant :h2} "Welcome to Masonic")
   (mui/typography
    {}
    "Masonic is a responsive, cross-platform web interface to ProjectMason, Team
    O's CS3733 project. Masonic implements a subset of service requests that
    patients may find useful, enabling interaction with the request system
    without needing to download and install Java or our full application.")
   (mui/typography {:variant :h4 :sx {:pt 4}} "Open Source")
   (mui/typography {} "Masonic utilizes many open source software libraries
   created and maintained by fantastic people.")
   (mui/grid {:container true :spacing 2 :sx {:mt 2}} (map fs-card fs))))
