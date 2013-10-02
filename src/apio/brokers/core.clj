(ns apio.brokers.core
  (:require [apio.core :as core])
  (:gen-class))

(def ^{:dynamic true} *connection*)

(defn connect []
  (let [backend-ns (core/current-broker-ns)
        connect-fn (ns-resolve (symbol backend-ns)
                               (symbol "initialize-connection"))]
    (connect-fn)))

(defn disconnect [conn]
  (let [backend-ns      (core/current-broker-ns)
        disconnect-fn   (ns-resolve (symbol backend-ns)
                                    (symbol "shutdown-connection"))]
    (disconnect-fn conn)))

(defmacro with-connection
  [& body]
  `(let [connection# (connect)]
     (println "Macro:" connection#)
     ~@body
     (disconnect connection#)))

(defmacro with-connection-and-handler
  [handler & body]
  `(let [backend-ns#    (core/current-broker-ns)
         connection#    (connect)
         attach-fn#     (ns-resolve (symbol backend-ns#)
                                    (symbol "attach-message-handler"))]
     (attach-fn# connection# ~handler)
     ~@body
     (disconnect connection#)))

(defmacro deliver-message
  [message]
  `(let [backend-ns#  (core/current-broker-ns)
         deliver-fn#  (ns-resolve (symbol backend-ns#)
                                  (symbol "deliver-message"))
         connection#  (connect)]
     (deliver-fn# connection# ~message)
     (disconnect connection#)))
