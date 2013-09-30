(ns apio.concurrency.queue
  (:import (java.util.concurrent LinkedBlockingQueue)))

;; Queue management.

(defn queue
  "Get channel."
  [num-procs]
  (LinkedBlockingQueue. num-procs))

(defn rcv
  [ch]
  (.take ch))

(defn snd
  [ch value]
  (.put ch value))
