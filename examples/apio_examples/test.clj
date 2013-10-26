(ns apio_examples.test
  (:require [apio.concurrency.threading :as thr]
            [apio.logging :as log])
  (:gen-class))

(defn hello-world
  [& args]
  (log/debug "Clojure Thread: %s Args: %s" (thr/current-thread-id) args)
  nil)
