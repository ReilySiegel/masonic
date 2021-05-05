(ns edu.wpi.teamo.masonic
  (:require [edu.wpi.teamo.masonic.types :as types]
            [edu.wpi.teamo.masonic.api :as api]
            [edu.wpi.teamo.masonic.server :as server]
            [integrant.core :as ig])
  (:import edu.wpi.teamo.Main))

(def config
  {::server/http   {::server/port 3000
                    ::server/env  (ig/ref ::api/env)}
   ::api/env       {}
   ::project-mason {}})

(def system (volatile! nil))

(defmethod ig/init-key ::project-mason [_ _]
  (future
    (try (Main/main (make-array String 0))
         (catch Exception e))
    (System/exit 0)))

(defmethod ig/halt-key! ::project-mason [_ f]
  (future-cancel f))

(defn start!
  ([args] (start!))
  ([]
   (types/install!)
   (when @system
     (vswap! system ig/halt!))
   (vreset! system (ig/init config))))

(defn start-headless!
  ([args] (start-headless!))
  ([]
   (types/install!)
   (when @system
     (vswap! system ig/halt!))
   (vreset! system (ig/init config [::server/http ::api/env]))))


(comment
  (start!)
  (start-headless!))
