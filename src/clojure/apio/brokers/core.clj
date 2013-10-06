(ns apio.brokers.core
  (:require [apio.core :as core]))

(def ^{:dynamic true} *connection*)

(defn connect []
  (let [backend-ns (core/current-broker-ns)
        connect-fn (ns-resolve (symbol backend-ns)
                               (symbol "acquire-channel"))]
    (connect-fn)))

(defn disconnect [conn]
  (let [backend-ns      (core/current-broker-ns)
        disconnect-fn   (ns-resolve (symbol backend-ns)
                                    (symbol "release-channel"))]
    (disconnect-fn conn)))

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
