(ns apio.logging
  (:import (apio Queue)
           (org.slf4j Logger LoggerFactory))
  (:require [apio.concurrency.threading :as thr])
  (:gen-class))

(def ^:dynamic *root-logger* (LoggerFactory/getLogger Logger/ROOT_LOGGER_NAME))

(defmacro with-logger
  [name & body]
  `(binding [*root-logger* (LoggerFactory/getLogger ~name)]
     ~@body))

(defn info
  [^String message & args]
  (doto *root-logger*
    (.info (apply format (into [message] args)))))

(defn error
  [^String message & args]
  (doto *root-logger*
    (.error (apply format (into [message] args)))))

(defn debug
  [^String message & args]
  (doto *root-logger*
    (.debug (apply format (into [message] args)))))
