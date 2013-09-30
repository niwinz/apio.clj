(ns apio.core
  (:require [clojure.string :as string]
            [apio.util :as u]
            [apio.concurrency.threading :as cc-threading]
            [apio.concurrency.queue :as cc-queue]
            [apio.concurrency.queue :as cc-util]))

(def ^:dynamic *config* {})

;; (defmacro with-config
;;   "Bind config and evalutea the body."
;;   [path & body]
;;   `(binding [*config* (u/read-configuration ~path)]
;;      (println "Reading configuration file:" ~path)
;;      ~@body))

(defn- is-clojure-task?
  "Checks if a name corresponds to clojure task or not."
  [^String name]
  (if (= (.indexOf name "/") -1) false true))

(defn resolve-task-by-name
  "Given a name, search in a properly way a corresponding
  task for that name."
  [^String task-name]
  (if (is-clojure-task? task-name)
    (let [[nsname, fnname] (string/split task-name #"/")]
      (ns-resolve (symbol nsname) (symbol fnname)))
    nil))
