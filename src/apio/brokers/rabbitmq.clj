(ns apio.brokers.rabbitmq
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [apio.core :as core]))

(def ^{:const true} default-exchange-name "")
(def ^{:const true} default-queue-name "niwi.be.queue.test")

(defrecord Connection [con chan])

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

    @settings))

(defn- connect
  "Connect to rabbitmq and returns a connection."
  []
  (let [conf (config->rabbitmq-settings)]
    (rmq/connect conf)))

(defn- channel
  "Open a new channel and returns it."
  [connection]
  (lch/open connection))


(defn- message-handler
  "Message handler wrapper."
  [handler]
  (let [wrapper (fn [ch, metadata, ^bytes payload]
                  (handler ch, metadata, (String. payload "UTF-8")))]
    wrapper))


(defn initialize-connection
  "Public api for initialize connection to rabbitmq and
  attach task handler to it.
  This function returns connection object that must be
  closed on program stops."
  [handler]
  (let [conn (connect)
        chan (channel conn)]
    (lq/declare chan default-queue-name
                :exclusive false :auto-delete true)
    (lc/subscribe chan default-queue-name
                  (message-handler handler) :auto-ack true)
    (Connection. conn chan)))
