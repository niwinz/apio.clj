(ns apio.brokers.rabbitmq
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [apio.core :as core]
            [apio.util :as util])
  (:gen-class))

(def ^{:const true} default-exchange-name "")
(def ^{:const true} default-queue-name "niwi.be.queue.test")
(def ^{:dynamic true} *connection*)
(def ^{:dynamic true} *channel*)

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
      (swap! settings assoc :port (util/parse-int (:port conf)))
      (swap! settings assoc :port (:port rmq/*default-config*)))

    (if (not (nil? (:username conf)))
      (swap! settings assoc :username (:username conf))
      (swap! settings assoc :username (:username rmq/*default-config*)))

    (if (not (nil? (:password conf)))
      (swap! settings assoc :password (:password conf))
      (swap! settings assoc :password (:password rmq/*default-config*)))
    @settings))

(defn- wrap-message-handler
  "Private method that create lexycal scope closure
  wrapping a message handler for convert a bytes payload
  into utf-8 encoded string."
  [handler]
  (let [wrapper (fn [ch, metadata, ^bytes payload]
                  (handler ch, metadata, (String. payload "UTF-8")))]
    wrapper))

(defn initialize-connection
  []
  (let [conf (config->rabbitmq-settings)]
    (try
      (rmq/connect conf)
      (catch Exception e
        (do
          (.printStackTrace e)
          (System/exit 1))))))

(defn initialize-channel [] (lch/open *connection*))

(defn attach-message-handler
  [handler]
  (let [wrapped-handler (wrap-message-handler handler)]
    (lq/declare *channel* default-queue-name :exclusive false :auto-delete true)
    (lc/subscribe *channel* default-queue-name wrapped-handler :auto-ack true)))

(defmacro with-connection
  [& body]
  `(if (not (bound? #'*connection*))
     ;; If connection vars are bound
     ;; connect, execute body and disconnect.
     (binding [*connection* (initialize-connection)]
       (binding [*channel*    (initialize-channel)]
         ~@body
         (rmq/close *channel*)
         (rmq/close *connection*)))

     ;; Else, only execute body but does not disconnect.
     (do ~@body)))

(defn deliver-message
  [^String message]
  (with-connection
    (lb/publish *channel* default-exchange-name default-queue-name message
                :content-type "text/plain" :type "greetings.hi")))

