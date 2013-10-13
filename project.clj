(defproject apio.clj "0.1.0"
  :description "Async task queue (like celery but for clojure and java)"
  :url "https://github.com/niwibe/apio.clj"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]

                 ;; RabbitMQ
                 [com.novemberain/langohr "1.5.0"]

                 ;; Logging
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-classic "1.0.13"]

                 ;; Redis
                 [com.taoensso/carmine "2.2.1"]]
                 ;; [com.taoensso/nippy "2.1.0"]
  :main apio.cli
  :profiles {:uberjar {:aot :all}}
  :source-paths ["src/clojure" "examples"]
  :java-source-paths ["src/java", "examples"]
  :jvm-opts ["-server" "-Xmx4g" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"])
