(ns apio.core
  (:require [apio.util :as u]
            [apio.concurrency :as c]))

;; Global constants

(def ^:dynamic *config*)
(def ^:dynamic *num-workers*)

(defmacro with-config
  "Bind config and evalutea the body."
  [path & body]
  `(binding [*config* (u/read-configuration ~path)]
     (println "Reading configuration file:" ~path)
     ~@body))

(defn initialize
  "Main entry point to start a worker"
  []
  (binding [*num-workers* (c/num-workers *config*)]
    (let [ch      (c/chan *num-workers*)
          thpool  (c/fixed-thread-pool *num-workers*)]

      ;; TODO: create result thread pool
      ;; TODO: start watching messages from RabbitMq

      (dotimes [n *num-workers*]
        (c/spawn-worker thpool ch n))

      (c/sleep 1000)

      (dotimes [n *num-workers*]
        (c/snd ch (str "msg" n))
        (c/sleep 300))

      (dotimes [n *num-workers*]
        (c/snd ch (str "msg" n))
        (c/sleep 300))

      (dotimes [n *num-workers*]
        (c/snd ch (str "msg" n))
        (c/sleep 300))

      (dotimes [n *num-workers*]
        (c/snd ch (str "msg" n))
        (c/sleep 300))

      (dotimes [n *num-workers*]
        (c/snd ch ""))

      (.shutdown thpool))))


