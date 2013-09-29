(ns apio.concurrency
  (import (java.util.concurrent Executors ExecutorService ThreadPoolExecutor)))

(defn ^Integer num-processes
  "Get number of processors."
  []
  (.availableProcessors (Runtime/getRuntime)))

(defn ^ExecutorService fixed-thread-pool
  "Creates a new fixed thread pool"
  [^Integer num-procs]
  (Executors/newFixedThreadPool num-procs))

(defn ^ExecutorService cached-thread-pool
  "Creates a standard thread pool."
  []
  (Executors/newCachedThreadPool))

(defn ^ExecutorService thread-pool-from-config
  "Reads a configuration map and retur corresponding
  thread pool."
  [config]
  (let [num-procs (-> config :concurrency :pool-size)
        num-procs (if (nil? num-procs) (num-processes) num-procs)]
    (println "Starting new thread-pool with " num-procs "threads.")
    (fixed-thread-pool num-procs)))
