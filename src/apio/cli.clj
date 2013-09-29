(ns apio.cli
  (:import (java.lang Thread))
  (:require [apio.core :as core]
            [apio.concurrency.threading :as cc-threading]
            [apio.concurrency.queue :as cc-queue]
            [apio.concurrency.util :as cc-util])
  (:gen-class))

(defn dispatcher
  [queue]
  (let [pool (cc-threading/fixed-pool (cc-util/max-workers core/*config*))]
    (loop []
      (let [task (cc-queue/rcv queue)]
        (when (instance? java.util.concurrent.Callable task)
          (do (cc-threading/spawn pool task) (recur)))))
    (cc-threading/shutdown-pool pool)))

(defn -main
  [path]
  (let [queue   (cc-queue/queue (cc-util/max-prefetch core/*config*))
        thr     (cc-threading/thread #(dispatcher queue))
        tasks   (map (fn [n]
                       (fn []
                         (println "Thread:" (.getId (Thread/currentThread)) "Processing:" n )
                         (cc-util/sleep (rand-int 1000)) 2)) (range 10))]
    (doseq [t tasks]
      (cc-queue/snd queue t))

    (cc-queue/snd queue 1)
    (.join thr)))
