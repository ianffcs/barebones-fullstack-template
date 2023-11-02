(ns user
  (:require [app.server :refer [-main]]
            [nrepl.server :refer [start-server
                                  stop-server]]
            [nrepl.transport :as transport]
            [portal.api :as p]))

(defonce p
  (p/open))

(defonce repl-server
  (atom nil))

(defn dev-main
  [& _]
  (swap! repl-server
         (fn [st]
           (some-> st stop-server)
           (start-server :bind "0.0.0.0"
                         :port 8888
                         :transport-fn transport/tty
                         :greeting-fn transport/tty-greeting)))
  (-> `shadow.cljs.devtools.server/start!
      requiring-resolve
      (apply []))
  (-> `shadow.cljs.devtools.api/watch
      requiring-resolve
      (apply [:app]))
  (-main))

