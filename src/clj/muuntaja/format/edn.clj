(ns muuntaja.format.edn
  (:require [clojure.edn :as edn])
  (:import (java.io InputStreamReader PushbackReader InputStream ByteArrayInputStream)))

(defn make-edn-decoder [options]
  (let [options (merge {:readers *data-readers*} options)]
    (fn [x ^String charset]
      (if (string? x)
        (edn/read-string options x)
        (edn/read options (PushbackReader. (InputStreamReader. ^InputStream x charset)))))))

(defn make-edn-encoder [_]
  (fn [data ^String charset]
    (ByteArrayInputStream.
      (.getBytes
        (pr-str data)
        charset))))

(defn make-edn-string-encoder [_]
  (fn [data _]
    (pr-str data)))

(defprotocol EncodeEdn
  (encode-edn [this charset]))

;;
;; format
;;

(def edn-type "application/edn")

(def edn-format
  {:decoder [make-edn-decoder]
   :encoder [make-edn-encoder]
   :encode-protocol [EncodeEdn encode-edn]})

(defn with-edn-format [options]
  (assoc-in options [:formats edn-type] edn-format))
