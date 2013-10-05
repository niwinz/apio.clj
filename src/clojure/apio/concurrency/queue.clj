(ns apio.concurrency.queue
  (:import (apio Queue)))

;; Queue management.

(defn queue
  "Get channel."
  [num-procs]
  (Queue. num-procs))

(defn rcv
  [ch]
  (.take ch))

(defn snd
  [ch value]
  (.put ch value))
