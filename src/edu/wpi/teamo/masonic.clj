(ns edu.wpi.teamo.masonic
  (:require [edu.wpi.teamo.masonic.api :as api]
            [edu.wpi.teamo.masonic.server :as server]
            [integrant.core :as ig])
  (:import [edu.wpi.teamo Main]))

(def config
  {::server/http {::server/port 3000
                  ::server/env  (ig/ref ::api/env)}
   ::api/env     {}})

(def system (volatile! nil))

(defn project-mason [args]
  (Main/main (make-array String 0)))

(defn restart! []
  (when @system
    (vswap! system ig/halt!))
  (vreset! system (ig/init config)))

(comment
  (restart!))
