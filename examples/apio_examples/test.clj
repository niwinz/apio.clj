(ns apio_examples.test
  (:require [apio.concurrency.threading :as thr])
  (:gen-class))

(defn hello-world
  [& args]
  (println "Clojure Thread:" (thr/current-thread-id) "Args:" args))
