(ns apio.concurrency.threading
  (:import (java.util.concurrent Executors ExecutorService)
           (java.lang Thread)))

(defn ^ExecutorService start-pool
  "Creates a new fixed thread pool"
  [^Integer num-procs]
  (Executors/newFixedThreadPool num-procs))

(defn shutdown-pool
  "Shutdown a thread pool"
  [pool]
  (println "Shutdown thread pool.")
  (.shutdown pool))

(defn thread
  [func]
  (let [th  (Thread. func)]
    (.start th)
    th))

(defn spawn
  [pool func]
  (.submit pool func))
