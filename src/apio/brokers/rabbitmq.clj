(ns apio.brokers.rabbitmq
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [apio.core :as core]))

(defn config->rabbitmq-settings
  "Read current configuration and build
  rabbitmq connection settings."
  []
  (let [conf      (core/current-config)
        conf      (-> conf :broker)
        settings  (atom {})]

    (if (not (nil? (:vhost conf)))
      (swap! settings assoc :vhost (:vhost conf))
      (swap! settings assoc :vhost (:vhost rmq/*default-config*)))

    (if (not (nil? (:host conf)))
      (swap! settings assoc :host (:host conf))
      (swap! settings assoc :host (:host rmq/*default-config*)))

    (if (not (nil? (:port conf)))
      (swap! settings assoc :port (:port conf))
      (swap! settings assoc :port (:port rmq/*default-config*)))

    (if (not (nil? (:username conf)))
      (swap! settings assoc :username (:username conf))
      (swap! settings assoc :username (:username rmq/*default-config*)))

    (if (not (nil? (:password conf)))
      (swap! settings assoc :password (:password conf))
      (swap! settings assoc :password (:password rmq/*default-config*)))

    settings))
