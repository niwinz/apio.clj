(ns apio.concurrency
  (:import (java.util.concurrent Executors ExecutorService ThreadPoolExecutor)
           (java.util.concurrent.locks ReentrantLock))
  (:require [clojure.core.async :as core.async]))

(defn ^Integer num-processes
  "Get number of processors."
  []
  (.availableProcessors (Runtime/getRuntime)))

(defn ^Integer num-workers
  "Get number of workers to start."
  [conf]
  (let [conf-workers (-> conf :concurrency :workers)]
    (if (nil? conf-workers)
      (num-processes)
      conf-workers)))

(defn ^ExecutorService fixed-thread-pool
  "Creates a new fixed thread pool"
  [^Integer num-procs]
  (Executors/newFixedThreadPool num-procs))

(defn ^ExecutorService cached-thread-pool
  "Creates a standard thread pool."
  []
  (Executors/newCachedThreadPool))

(defn shutdown-thread-pool
  "Shutdown a thread pool"
  [pool]
  (println "Shutdown thread pool.")
  (.shutdown pool))

(defn chan
  "Get channel."
  [num-procs]
  (core.async/chan num-procs))

(defn rcv
  [ch]
  (core.async/<!! ch))

(defn snd
  [ch value]
  (core.async/>!! ch value))

(defmacro with-lock
  [& body]
  `(let [lock-instance# (ReentrantLock.)]
     (.lock lock-instance#)
     (try
       ~@body
       (finally (.unlock lock-instance#)))))

(defn spawn-worker
  "Spawn new worker"
  [thpool ch n]
  (let [processor (fn [data i]
                    (println "[" n "/" i "] Received:" data))
        callable (fn []
                    (loop [i 0]
                      (let [data (core.async/<!! ch)]
                        (if (empty? data)
                          (do (println "Shutdown worker:" n))
                          (do (processor data i) (recur (inc i)))))))]
    (println "Starting worker:" n)
    (.submit thpool callable)))

(defn sleep
  [ms]
  (java.lang.Thread/sleep ms))
