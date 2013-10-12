(ns apio.brokers.rabbitmq
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [apio.concurrency.threading :as thr]
            [apio.core :as core]
            [apio.util :as util])
  (:gen-class))

(def ^{:const true} default-exchange-name "")
(def ^{:const true} default-queue-name "niwi.be.queue.test")

(defn config->rabbitmq-settings
  "Read current configuration and build
  rabbitmq connection settings."
  []
  (let [conf      (core/current-config)
        conf      (-> conf :broker)
        settings  (atom {})]
    (if-not (nil? (:vhost conf))
      (swap! settings assoc :vhost (:vhost conf))
      (swap! settings assoc :vhost (:vhost rmq/*default-config*)))
    (if-not (nil? (:host conf))
      (swap! settings assoc :host (:host conf))
      (swap! settings assoc :host (:host rmq/*default-config*)))
    (if-not (nil? (:port conf))
      (swap! settings assoc :port (util/parse-int (:port conf)))
      (swap! settings assoc :port (:port rmq/*default-config*)))
    (if-not (nil? (:username conf))
      (swap! settings assoc :username (:username conf))
      (swap! settings assoc :username (:username rmq/*default-config*)))
    (if-not (nil? (:password conf))
      (swap! settings assoc :password (:password conf))
      (swap! settings assoc :password (:password rmq/*default-config*)))
    @settings))

;; Backend connection functions

(defn- connect [conf]
  (try
    (rmq/connect conf)
    (catch java.net.ConnectException e
      (do
        (util/print-stacktrace e)
        (thr/sleep 300)

        (println "\nSeems your connection parameters are wrong"
                 "or rabbitmq service is down!")
          (core/exit 1)))))

(defn- channel [conn]
  (lch/open conn))

(defn- wrap-message-handler
  "Private method that create lexycal scope closure
  wrapping a message handler for convert a bytes payload
  into utf-8 encoded string."
  [handler]
  (let [wrapper (fn [ch, metadata, ^bytes payload]
                  (handler (String. payload "UTF-8") metadata))]
    wrapper))

(defrecord Connection [conn chan])

;; Connection management

(def ^{:private true :dynamic true} *connection* (atom nil))
(def ^{:private true :dynamic true} *channels* (ref []))

;; High level connection functions.
;; Public API

(defn acquire-channel
  "Get channel from pool or create new
  if pool is empty."
  []
  (if (nil? (deref *connection*))
    (reset! *connection* (connect (config->rabbitmq-settings))))
  (dosync
    (ensure *channels*)
    (let [ch    (peek @*channels*)
          rs    (if (nil? ch) nil (pop @*channels*))]
      (if rs
        (do (ref-set *channels* rs) (Connection. *connection* ch))
        (do (Connection. *connection* (channel @*connection*)))))))

(defn release-channel
  "Release a used channel"
  [^Connection c]
  (let [chan  (:chan c)
        ok    (dosync
                (ensure *channels*)
                (if (< (count @*channels*) 5)
                  (do (alter *channels* conj chan) true)
                  false))]
    (if-not ok (rmq/close chan))))

(defn attach-message-handler
  [^Connection connection, handler]
  (let [wrapped-handler (wrap-message-handler handler)
        chan            (:chan connection)]
    (lq/declare chan default-queue-name :exclusive false :auto-delete true)
    (lc/subscribe chan default-queue-name wrapped-handler :auto-ack true)))

(defn deliver-message
  [^Connection connection, ^String message]
  (let [chan (:chan connection)]
    (lb/publish chan default-exchange-name default-queue-name message
                :content-type "text/plain" :type "greetings.hi")))
