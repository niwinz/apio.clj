(ns apio.cli
  (:import (java.lang Thread)
           (java.util.concurrent Callable))
  (:require [apio.core :as core]
            [apio.util :as util]
            [apio.concurrency.threading :as thr]
            [apio.concurrency.semaphore :as sem]
            [apio.concurrency.queue :as q]
            [apio.brokers.rabbitmq :as mq])
  (:gen-class))

(defn dispatch-one-task
  [pool task semaphore]
  (sem/acquire semaphore)
  (let [task-wrapper (fn []
                       (task)
                       (sem/release semaphore))]
    (thr/spawn pool task-wrapper)))

(defn task-dispatcher
  [queue]
  (let [numworkers  (util/max-workers (core/current-config))
        pool        (thr/start-pool numworkers)
        semaphore   (sem/semaphore numworkers)]
    (loop []
      (let [task (q/rcv queue)]
        (when (instance? Callable task)
          (do (dispatch-one-task pool task semaphore) (recur)))))
    (thr/shutdown-pool pool)))

(defn messages-dispatcher
  [queue]
  (let [task-generator  (fn [msg] #(println "Thread:" (thr/current-thread-id) "Msg:" msg))
        wrapper (fn [d & args] (q/snd queue (task-generator d)))]
    wrapper))

(defn -main
  [path & args]
  (core/with-config path
    (let [queue   (q/queue (util/max-prefetch core/*config*))
          thr     (thr/thread #(task-dispatcher queue))]

      ;; Add hook for keyboard interruption (sigint) and
      ;; properly close RabbitMQ connection.
      ;; (let [hook (proxy [Thread] [] (run [] (mq/finish-connection mq-conn)))]
      ;;   (.addShutdownHook (Runtime/getRuntime) hook))

      (mq/with-connection
        (mq/attach-message-handler (messages-dispatcher queue))

        (thr/sleep 1000)
        (mq/deliver-message "Hello 1")
        (thr/sleep 1000)
        (mq/deliver-message "Hello 2")
        (thr/sleep 1000)
        (mq/deliver-message "Hello 3")
        (thr/sleep 1000)
        (mq/deliver-message "Hello 4")
        (thr/sleep 1000)
        (mq/deliver-message "Hello 5")

        (thr/join thr)))))
