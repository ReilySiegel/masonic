(ns edu.wpi.teamo.masonic.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp]
            [edu.wpi.teamo.masonic.client.ui.root :as root]
            [edu.wpi.teamo.masonic.client.app :refer [app]]))

(defn ^:export init! []
  (app/mount! app root/Root "app"))

(defn ^:export refresh! []
  (app/mount! app root/Root "app")
  (comp/refresh-dynamic-queries! app))
