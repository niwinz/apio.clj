(ns apio.concurrency.core
  (import (java.util.concurrent ForkJoinPool)))

;; TODO: initialize forkjoin pool in dynamic context
;; using dynamic loaded configuration.
(def *thread-pool* (ForkJoinPool.))
