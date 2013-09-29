(ns apio.cli
  (:require [apio.core :as core])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [path]
  (core/with-config path
    (println "Config file:", core/*config*)))
