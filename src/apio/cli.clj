(ns apio.cli
  (:import (java.lang Thread)
           (java.util.concurrent Callable))
  (:require [apio.core :as core]
            [apio.util :as util]
            [apio.concurrency.threading :as thr]
            [apio.concurrency.semaphore :as sem]
            [apio.concurrency.queue :as q])
  (:gen-class))

(defn dispatch-task
  [pool task semaphore]
  (sem/acquire semaphore)
  (let [task-wrapper (fn [] (task) (sem/release semaphore))]
    (thr/spawn pool task-wrapper)))

;; (defn sempahore-status-reporter
;;   [semaphore]
;;   (loop []
;;     (println "Current semaphore status:" (.availablePermits semaphore))
;;     (thr/sleep 300)
;;     (recur)))

(defn dispatcher-loop
  [queue]
  (let [pool        (thr/start-pool (util/max-workers core/*config*))
        semaphore   (sem/semaphore  (util/max-workers core/*config*))]
    (loop []
      (let [task (q/rcv queue)]
        (when (instance? Callable task)
          (do (dispatch-task pool task semaphore) (recur)))))
    (thr/shutdown-pool pool)))

(defn -main
  [path]
  (let [queue  (q/queue (util/max-prefetch core/*config*))
        thr    (thr/thread #(dispatcher-loop queue))
        tasks  (map (fn [n]
                       (fn []
                         (println "Thread:" (thr/current-thread-id) "Processing:" n )
                         (thr/sleep (rand-int 1000)) 2)) (range 10))]
    (doseq [t tasks]
      (q/snd queue t))

    ;; Send non callable value to shutdown dispatcher.
    (q/snd queue 1)
    (thr/join thr)))
