(ns lcmap.tiffany.file
  (:require [cheshire.core :as json]
            [clojure.spec.alpha :as spec]
            [clojure.java.io :as io]
            [lcmap.tiffany.validation :as validation]))

(spec/def ::file_exists #(.exists (io/file %)))
(spec/def ::file_path (spec/and string? ::file_exists))

(defn read-json
  "Returns a lazy sequence"
  [infile]
  (let [filepath (validation/check! ::file_path infile)]
    (json/parse-stream (io/reader filepath))))
