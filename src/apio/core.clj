(ns apio.core
  (:require [clojure.string :as string]
            [apio.util :as util]))

(def ^:dynamic *config* (atom {}))

(defmacro with-config
  "Bind config and evalutea the body."
  [path & body]
  `(let [conf# (util/read-configuration ~path)]
     (reset! *config* conf#)
     (println "Setting config to:" conf#)
     ~@body))

(defn current-config
  "Get current config value."
  []
  (deref *config*))

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
