(ns apio.util
  (:require [apio.config.inifiles :as ini]
            [apio.concurrency.threading :refer [num-processes]]))

(def ^:dynamic *default-max-prefetch* 3)
(def ^:dynamic *default-max-workers* (num-processes))

(defn read-configuration
  "Helper function for read config file."
  [path]
  (ini/read-ini path :keywordize? true))

(defn ^Integer max-prefetch
  "Get a main queue size. Like MAX_PREFETCH configuration
  on celery."
  [conf]
  (let [prefetch (-> conf :broker :max-prefetch)]
    (if (nil? prefetch) *default-max-prefetch* prefetch)))

(defn ^Integer max-workers
  "Get number of workers to start."
  [conf]
  (let [conf-workers (-> conf :concurrency :workers)]
    (if (nil? conf-workers) *default-max-workers* conf-workers)))
