(ns edu.wpi.teamo.masonic
  (:require 
   [edu.wpi.teamo.masonic.api :as api]
   [edu.wpi.teamo.masonic.server :as server]
   [integrant.core :as ig])
  (:import edu.wpi.teamo.Main)
  (:gen-class))

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
   (println "Starting all Services...")
   (when @system
     (vswap! system ig/halt!))
   (vreset! system (ig/init config))))

(defn start-headless!
  ([args] (start-headless!))
  ([]
   (println "Starting Headless...")
   (when @system
     (vswap! system ig/halt!))
   (vreset! system (ig/init config [::server/http ::api/env]))))

(defn start-mason!
  ([args] (start-mason!))
  ([]
   (println "Starting App...")
   (when @system
     (vswap! system ig/halt!))
   (vreset! system (ig/init config [::project-mason]))))

(defn -main [& args]
  (let [mode  (first args)
        modes "Valid service modes are [all, app, web]"]
    (case mode
      nil
      (do (println "No service mode specified!")
          (deref (::project-mason (start!))))
      "app"
      @(::project-mason (start-mason!))
      "all"
      @(::project-mason (start!))
      "web"
      (start-headless!)
      "help"
      (println "java -jar mason.jar [service-mode]\n" modes)
      (println "Invalid service mode!\n" modes))))

(comment
  (start!)
  (start-headless!))
