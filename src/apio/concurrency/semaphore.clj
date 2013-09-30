(ns apio.concurrency.semaphore
  (:import java.util.concurrent.Semaphore))

(defn ^Semaphore semaphore
  "Creates a new semaphore."
  [n]
  (Semaphore. n))

(defn acquire
  "Acquires a permit from this semaphore, blocking until one
  is available, or the thread is interrupted."
  ([sem] (.acquire sem))
  ([sem n] (.acquire sem n)))

(defn release
  "Releases a permit, returning it to the semaphore."
  ([sem] (.release sem))
  ([sem n] (.release sem n)))
