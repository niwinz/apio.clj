(ns apio.concurrency.util)

(defn ^Integer num-processes
  "Get number of processors."
  []
  (.availableProcessors (Runtime/getRuntime)))

(def ^:dynamic *default-max-prefetch* 3)
(def ^:dynamic *default-max-workers* (num-processes))

(defn sleep
  [ms]
  (java.lang.Thread/sleep ms))

(defn ^Integer max-workers
  "Get number of workers to start."
  [conf]
  (let [conf-workers (-> conf :concurrency :workers)]
    (if (nil? conf-workers) *default-max-workers* conf-workers)))

(defn ^Integer max-prefetch
  "Get a main queue size. Like MAX_PREFETCH configuration
  on celery."
  [conf]
  (let [prefetch (-> conf :broker :max-prefetch)]
    (if (nil? prefetch) *default-max-prefetch* prefetch)))
