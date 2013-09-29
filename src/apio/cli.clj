(ns apio.cli
  (:require [apio.util :as util])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [configfile]
  (let [data (util/read-configuration configfile)]
    (println "Config file:", data)))
