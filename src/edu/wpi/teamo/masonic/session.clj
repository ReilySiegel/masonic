(ns edu.wpi.teamo.masonic.session
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.account :as account])
  (:import edu.wpi.teamo.database.account.Account))

(pco/defmutation login [{::account/keys [username password]}]
  {::valid?           (= password (.getPasswordHash (Account/getByUsername username)))
   ::account/username username})
