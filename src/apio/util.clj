(ns apio.util)

(defn parse-int [s]
  (Integer. (re-find  #"\d+" s )))

(defn print-stacktrace [exception]
  (.printStackTrace exception))

