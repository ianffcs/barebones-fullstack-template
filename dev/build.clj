(ns build
  (:require [clojure.tools.build.api :as b]
            [shadow.cljs.devtools.api :as shadow.api]
            [shadow.cljs.devtools.server :as shadow.server]))

(defn build []
  (let [lib 'com.ianffcs/template-app
        class-dir "target/classes"
        uber-file "target/template-app.jar"
        basis (b/create-basis {:project "deps.edn"})]
    (b/delete {:path "target"})
    (shadow.server/start!)
    (shadow.api/release :app)
    (shadow.server/stop!)
    (b/write-pom {:class-dir class-dir
                  :lib       lib
                  :version   "0.0.1"
                  :basis     basis})
    (b/copy-dir {:src-dirs   (:paths basis)
                 :target-dir class-dir})
    (b/compile-clj {:basis     basis
                    :class-dir class-dir
                    :ns-compile '[app.server]})
    (b/uber {:class-dir class-dir
             :main      'app.server
             :uber-file uber-file
             :basis     basis})))

(defn -main [& _]
  (build)
  (shutdown-agents))

(comment
  (build))
