(ns apio.cli
  (:import (java.lang Thread))
  (:require [apio.core :as core]
            [apio.concurrency.threading :as cthreading]
            [apio.concurrency.queue :as cqueue]
            [apio.concurrency.util :as cutil])
  (:gen-class))

(defn dispatcher
  [queue]
  (let [pool (cthreading/start-pool (cutil/max-workers core/*config*))]
    (loop []
      (let [task (cqueue/rcv queue)]
        (when (instance? java.util.concurrent.Callable task)
          (do (cthreading/spawn pool task) (recur)))))
    (cthreading/shutdown-pool pool)))

(defn -main
  [path]
  (let [queue   (cqueue/queue (cutil/max-prefetch core/*config*))
        thr     (cthreading/thread #(dispatcher queue))
        tasks   (map (fn [n]
                       (fn []
                         (println "Thread:" (.getId (Thread/currentThread)) "Processing:" n )
                         (cutil/sleep (rand-int 1000)) 2)) (range 10))]
    (doseq [t tasks]
      (cqueue/snd queue t))

    ;; Send non callable value to shutdown dispatcher.
    (cqueue/snd queue 1)
    (.join thr)))
