(ns apio.core
  (:require [apio.util :as util]
            [apio.concurrency :as c]))

;; Global constants

(def ^:dynamic *config*)
(def ^:dynamic *thread-pool*)

(defmacro with-config
  "Bind config and evalutea the body."
  [path & body]
  `(binding [*config* (util/read-configuration ~path)
             *thread-pool* (c/thread-pool-from-config *config*)]
     ~@body))
