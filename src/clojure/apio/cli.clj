(ns apio.cli
  (:import apio.Queue)
  (:require [apio.core :as core]
            [apio.concurrency.threading :as thr]
            [apio.concurrency.semaphore :as sem]
            [apio.concurrency.queue :as q]
            [apio.tasks :as tasks]
            [apio.logging :as log]
            [apio.brokers.core :as brk])
  (:gen-class))

(defn- dispatch-task
  [pool task semaphore]
  (sem/acquire semaphore)
  (let [task-wrapper (fn []
                       (task)
                       (sem/release semaphore))]
    (thr/spawn pool task-wrapper)))

(defn- dispatcher
  [^Queue queue]
  (let [numworkers  (core/max-workers)
        pool        (thr/start-pool numworkers)
        semaphore   (sem/semaphore numworkers)]
    (loop []
      (let [task (q/rcv queue)]
        (do (dispatch-task pool task semaphore) (recur))))
    (thr/shutdown-pool pool)))

(defn- messages-dispatcher
  "Broker callback for receive messages."
  [^Queue queue]
  (let [dispatcher (fn [^String message & args]
                     (let [unit (tasks/exec-unit-from-message message)]
                       (if-not (nil? unit)
                         (q/snd queue unit)
                         (log/info "Message %s does not corresponds to any task" message))))]
    dispatcher))

(defn -main
  [path & args]
  (core/with-config path
    (let [queue   (q/queue (core/max-prefetch))
          thr     (thr/thread #(dispatcher queue))]

      ;; Add hook for keyboard interruption (sigint) and
      ;; properly close RabbitMQ connection.
      ;; (let [hook (proxy [Thread] [] (run [] (mq/finish-connection mq-conn)))]
      ;;   (.addShutdownHook (Runtime/getRuntime) hook))

      (brk/with-connection-and-handler (messages-dispatcher queue)
        (tasks/send-task "hello-world" 1 2 3)
        (tasks/send-task "hello-world" 4 5 6)
        (tasks/send-task "hello-world" 4 5 6)
        (tasks/send-task "hello-world" 4 5 6)
        (tasks/send-task "hello-world" 4 5 6)
        (tasks/send-task "hello-world" 4 5 6)
        ;; (tasks/send-task "hello-java" 7 8 9 {"a" 1})
        (tasks/send-task "hello-world2" 10 11 12)
        (thr/join thr)))))
