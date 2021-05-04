(ns edu.wpi.teamo.masonic.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [edu.wpi.teamo.masonic.client.app :refer [app]]
            [edu.wpi.teamo.masonic.client.ui.root :as root]
            [edu.wpi.teamo.masonic.types :as types]
            [tick.locale-en-us]))

(defn ^:export init! []
  (app/set-root! app root/Root {:initialize-state? true})
  (dr/change-route! app [""])
  (app/mount! app root/Root "app"))

(defn ^:export refresh! []
  (app/mount! app root/Root "app")
  (comp/refresh-dynamic-queries! app))
