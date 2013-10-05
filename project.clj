(defproject apio.clj "0.1.0"
  :description "Async task queue (like celery but for clojure and java)"
  :url "https://github.com/niwibe/apio.clj"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]
                 [com.novemberain/langohr "1.5.0"]]

                 ;; [com.taoensso/nippy "2.1.0"]
                 ;; [com.taoensso/carmine "2.2.1"]
  :main apio.cli
  ;; :aot :all
  :profiles {:uberjar {:aot :all}}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :jvm-opts ["-Xmx2g"])
