(ns apio_examples.test
  (:require [apio.concurrency.threading :as thr])
  (:gen-class))

(defn hellow-world
  [& args]
  (println "Thread:" (thr/current-thread-id) "Args:" args))
