(ns apio.util
  (:require [apio.config.inifiles :as ini]))

(defn read-configuration
  "Helper function for read config file."
  [path]
  (ini/read-ini path :keywordize? true))
