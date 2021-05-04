(ns edu.wpi.teamo.masonic.request.sanitation
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.request :as request])
  (:import edu.wpi.teamo.database.request.SanitationRequest))

(defn request->m [^SanitationRequest obj]
  (merge
   (request/request->m obj)
   {::recurring? (.isRecurring obj)}))

(pco/defresolver all []
  {::pco/output [{::all (into request/outputs [::recurring?])}]}
  {::all (->> (SanitationRequest/getAll)
              (.iterator)
              iterator-seq
              (mapv request->m))})
