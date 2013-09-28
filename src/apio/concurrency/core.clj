(ns apio.concurrency.core
  (import (java.util.concurrent Executors)))

;; TODO: initialize forkjoin pool in dynamic context
;; using dynamic loaded configuration.

(defn num-processes [] (.availableProcessors Runtime/getRuntime))
(def *thread-pool* (Executors/newFixedThreadPool (* (num-processes) 2)))
