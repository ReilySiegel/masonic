(ns edu.wpi.teamo.masonic.request.food
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [edu.wpi.teamo.masonic.request :as request])
  (:import edu.wpi.teamo.database.request.FoodRequest))

(defn request->m [^FoodRequest obj]
  (merge
   (request/request->m obj)
   {::dietary-restrictions (.getdR obj)
    ::appetizer            (.getAppetizer obj)
    ::entre                (.getEntre obj)
    ::dessert              (.getDessert obj)}))

(pco/defresolver all []
  {::pco/output [{::all (into request/outputs
                              [::dietary-restrictions
                               ::appetizer
                               ::entre
                               ::dessert])}]}
  {::all (->> (FoodRequest/getAll)
              (.iterator)
              iterator-seq
              (mapv request->m))})
