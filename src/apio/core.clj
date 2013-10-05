(ns apio.core
  (:require [apio.config.inifiles :as ini]
            [apio.concurrency.threading :refer [num-processes]]
            [apio.util :as util]))

(def ^:dynamic *config* (atom {}))
(def ^:dynamic *default-max-prefetch* 3)
(def ^:dynamic *default-max-workers* (num-processes))

;; Configuration related methods.

(defn read-configuration
  "Helper function for read config file."
  [path]
  (ini/read-ini path :keywordize? true))

(defn current-config
  "Get current configuration."
  []
  (deref *config*))

(defmacro with-config
  "Bind config and evalutea the body."
  [path & body]
  `(let [conf# (read-configuration ~path)]
     (reset! *config* conf#)
     (println "Setting config to:" conf#)
     ~@body))

(defn ^Integer max-prefetch
  "Get a main queue size. Like MAX_PREFETCH configuration
  on celery."
  []
  (let [prefetch (-> (current-config) :broker :max-prefetch)]
    (if (nil? prefetch) *default-max-prefetch* prefetch)))

(defn ^Integer max-workers
  "Get number of workers to start."
  []
  (let [conf-workers (-> (current-config) :concurrency :workers)]
    (if (nil? conf-workers) *default-max-workers* conf-workers)))

(defn current-broker-ns
  "Given current config, return a currently selected
  broker backend namespace."
  []
  (let [backendns (-> (current-config) :broker :backend)]
    (if (nil? backendns) "apio.brokers.rabbitmq" backendns)))

;; System util methods.

(defn exit [returncode]
  (System/exit returncode))
