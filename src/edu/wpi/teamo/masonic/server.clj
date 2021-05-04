(ns edu.wpi.teamo.masonic.server
  (:require [com.fulcrologic.fulcro.server.api-middleware :as fm]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [hiccup.page :as hiccup]
            [integrant.core :as ig]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :as rmd]
            [ring.middleware.session.memory :as mem]
            [ring.util.response :as resp]))

(defonce sessions (atom {}))

(defn index [csrf-token]
  (hiccup/html5
   [:html {:lang "en"}
    [:head {:lang "en"}
     [:title "Masonic"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
     [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap"}]
     [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
     [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
    [:body {:style "background-color: #2E3440"}
     [:div#app]
     [:script {:src "/js/main.js"}]]]))

(defn index-handler [{:keys [uri anti-forgery-token] :as req}]
  (-> (resp/response (index anti-forgery-token))
      (resp/content-type "text/html")))

(defn- wrap-api [handler uri env]
  (fn [req]
    (if (= uri (:uri req))
      (fm/generate-response
       (let [parse-result
             (try
               (p.eql/process (merge env {:ring/request req}) (:transit-params req))
               (catch Exception e e))]
         (if (instance? Throwable parse-result)
           {:status 500
            :body   {:error (ex-message parse-result)}}
           (merge {:status 200
                   :body   parse-result}
                  (fm/apply-response-augmentations parse-result)))))
      (handler req))))


(defmethod ig/init-key ::http [_ {::keys [port env]}]
  (http/run-server (-> index-handler
                       (wrap-api "/api" env)
                       (fm/wrap-transit-params)
                       (fm/wrap-transit-response)
                       (rmd/wrap-defaults {:static  {:resources "public"}
                                           :session {:store (mem/memory-store sessions)}}))
                   {:port port}))

(defmethod ig/halt-key! ::http [_ server]
  (server))
