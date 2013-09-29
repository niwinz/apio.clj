(ns apio.cli
  (:require [apio.core :as core]
            [apio.concurrency :as c])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [path]
  (core/with-config path
    (core/initialize)))
