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

(defn join
  [thr]
  (.join thr))

(defn current-thread-id
  []
  (.getId (Thread/currentThread)))

(defn spawn
  [pool func]
  (.submit pool func))

(defn ^Integer num-processes
  "Get number of processors."
  []
  (.availableProcessors (Runtime/getRuntime)))

(defn sleep
  [ms]
  (java.lang.Thread/sleep ms))

