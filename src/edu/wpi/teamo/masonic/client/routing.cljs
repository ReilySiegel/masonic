(ns edu.wpi.teamo.masonic.client.routing
  (:require [clojure.string :as str]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [edu.wpi.teamo.masonic.client.app :refer [app]]
            [pushy.core :as pushy]
            [com.fulcrologic.fulcro.mutations :as m]))

(defonce history
  (pushy/pushy
   (fn [p]
     (let [route-segments-raw (vec (rest (str/split p "/")))
           route-segments     (if (empty? route-segments-raw)
                                [""]
                                route-segments-raw)]
       (dr/change-route app route-segments)))
   identity))

(defn start! []
  (pushy/start! history))

(defn route-to!
  "Change routes to the given route-string (e.g. \"/home\"."
  [route-string]
  (pushy/set-token! history route-string))

(m/defmutation route-to
  [{:keys [route]}]
  (action [_]
          (route-to! (str \/ (str/join \/ route)))))
