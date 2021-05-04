(ns edu.wpi.teamo.masonic.account
  (:require [com.wsscode.pathom3.connect.operation :as pco])
  #?(:clj (:import edu.wpi.teamo.database.account.Account)))

(def output [::username
             ::admin?
             ::role
             ::first-name
             ::last-name])
#?(:clj
   (defn account->m [^Account obj]
     {::username   (.getUsername obj)
      ::admin?     (.isAdmin obj)
      ::role       (.getRole obj)
      ::first-name (.getFirstName obj)
      ::last-name  (.getLastName obj)}))

#?(:clj
   (pco/defresolver by-username [{::keys [username]}]
     {::pco/output output}
     (account->m (Account/getByUsername "admin"))))

#?(:clj
   (pco/defresolver all []
     {::pco/output [{::all output}]}
     {::all (->> (Account/getAll)
                 .iterator
                 iterator-seq
                 (mapv account->m))}))

(pco/defresolver full-name [{::keys [first-name last-name]}]
  {::name (str first-name " " last-name)})
