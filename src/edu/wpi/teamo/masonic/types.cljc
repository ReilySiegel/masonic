(ns edu.wpi.teamo.masonic.types
  (:require [com.fulcrologic.fulcro.algorithms.transit :as transit]
            [tick.alpha.api :as tick]
            [time-literals.read-write :as tl.rw]
            #?(:cljs [java.time :refer [LocalDateTime]]))
  #?(:clj (:import (java.time LocalDateTime))))

(defn install! []
  (transit/install-type-handler!
   (transit/type-handler LocalDateTime "time/date-time"
                         (fn [^LocalDateTime dt] (str dt))
                         (fn decode [dts]
                           (try ((get tl.rw/tags 'time/date-time) dts)
                                (catch #?(:clj Exception :cljs js/Error) e
                                  (decode (apply str (rest dts)))))))))
