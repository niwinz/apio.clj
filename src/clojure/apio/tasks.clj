(ns apio.tasks
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [apio.core :as core]
            [apio.util :as util]
            [apio.brokers.core :as brk])
  (:gen-class))

;; High level functions

(defn- is-clojure-task?
  "Checks if a name corresponds to clojure task or not."
  [^String name]
  (if (= (.indexOf name "/") -1) false true))

(defn send-task
  "Build a task signature and send it to a broker."
  [^String name & params]
  (let [task-id         (util/uuid)
        signature       {:name name, :params params, :task-id task-id}
        signature-json  (json/write-str signature)]
    (brk/deliver-message signature-json)))

;; Low level functions

(defn make-clojure-unit
  [^clojure.lang.IFn unit]
  (let [wrapper (fn [& args]
                  (try {:result (apply unit args)}
                    (catch Exception e (do (.printStackTrace e) {:error (.getMessage e)}))))]
    (fn [& args]
      (let [result (apply wrapper args)]
        (println "[clj] Result:" result)
        result))))

(defn make-java-unit
  [^apio.TaskUnit unit]
  (fn [& args]
    (let [result (.exec unit (java.util.ArrayList. (vec args)))
          result (into {} result)]
      (println "[java] Result:" result)
      result)))

(defn resolve-fn-by-name
  "Given a name, search in a properly way a registred
  function for that name."
  [^String task-name]
  (let [all-tasks (-> (core/current-config) :tasks)
        full-name ((keyword task-name) all-tasks)]
    (if (not (nil? full-name))
      (if (is-clojure-task? full-name)
        (let [[nsname, fnname]  (string/split full-name #"/")]
          (make-clojure-unit (ns-resolve (symbol nsname) (symbol fnname))))
        (let [cls       (Class/forName full-name)
              instance  (.newInstance cls)]
          (make-java-unit instance))))))

(defn exec-unit-from-message
  "Given json encoded message, resolve it into
  one registred function and return it.
  At the moment only works with clojure functions."
  [^String message]
  (let [data      (json/read-str message :key-fn keyword)
        callable  (resolve-fn-by-name (:name data))]
    (if (not (nil? callable))
      (fn [] (apply callable (:params data))) nil)))
