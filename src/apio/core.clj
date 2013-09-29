(ns apio.core
  (:require [apio.util :as u]
            [apio.concurrency.threading :as cc-threading]
            [apio.concurrency.queue :as cc-queue]
            [apio.concurrency.queue :as cc-util]))

(def ^:dynamic *config* {})

(defmacro with-config
  "Bind config and evalutea the body."
  [path & body]
  `(binding [*config* (u/read-configuration ~path)]
     (println "Reading configuration file:" ~path)
     ~@body))
