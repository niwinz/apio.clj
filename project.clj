(defproject apio.clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]
                 [com.novemberain/langohr "1.5.0"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]]

                 ;; [com.taoensso/nippy "2.1.0"]
                 ;; [com.taoensso/carmine "2.2.1"]
  :main apio.cli
  :profiles {:uberjar {:aot :all}})
