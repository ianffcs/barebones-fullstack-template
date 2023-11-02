(ns app.server
  (:gen-class)
  (:require [hiccup2.core :as h]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as interceptor]
            [shadow.cljs.devtools.server :as server]))


(defn index
  [_req]
  (let [html [:html {:lang "en"}
              [:head
               [:meta {:charset "UTF-8"}]
               [:link {:rel "icon" :href "data:"}]
               [:meta {:name    "viewport"
                       :content "width=device-width, initial-scale=1.0"}]
               [:meta {:name    "theme-color"
                       :content "#000000"}]
               [:meta {:name    "description"
                       :content "A simple full-stack clojure app"}]
               [:title "app"]]
              [:body
               [:div {:id "app"} "loading ..."
                [:script {:src "/app/main.js"}]]]]]
    {:body    (->> html
                   (h/html {:mode :html})
                   (str "<!DOCTYPE html>\n"))
     :headers {"Content-Type" "text/html"}
     :status  200}))

(def system
  (-> {::http/port      8080
       ::http/file-path "target/classes/public"
       ::http/host      "localhost"
       ::http/type      :jetty
       ::http/join?     false
       ::http/secure-headers {:content-security-policy-settings ""}
       ::http/resource-path "public"
       ::http/routes (-> `#{["/" :get index]}
                         route/expand-routes)}
      http/default-interceptors
      (#(update %
                ::http/interceptors
                (fn [itxs]
                  (cons (-> {:name  ::request-add-config-itx
                             :enter (fn [ctx]
                                      (update ctx :request merge %))}
                            interceptor/interceptor)
                        itxs))))
      http/dev-interceptors
      http/create-server))

(defonce state
  (atom nil))

(defn -main
  [& _]
  {:shadow/requires-server true}
  (server/start!)
  (swap! state
         (fn [st]
           (some-> st http/stop)
           (http/start system)))
  (println "started: "
           8080))