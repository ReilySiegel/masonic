(ns edu.wpi.teamo.masonic.client.app
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.networking.http-remote :as http]
            [edu.wpi.teamo.masonic.types :as types]))

(types/install!)

(defonce app (app/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}}))
