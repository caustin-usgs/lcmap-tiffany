(ns lcmap.tiffany.util
  (:require [environ.core :as environ]
            [clojure.spec.alpha :as spec]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [lcmap.tiffany.file :as file]))


(defn flatten-vals
  "Flatten the values for a collection of hash-maps"
  [coll mapkey]
  (let [coll_vals (map (fn [i] (vals i)) coll)
        vals_flat (flatten coll_vals)]
    (map mapkey vals_flat)))

;; add-usr-path and amend-usr-path blatantly ripped off from the
;; USGS-EROS/lcmap-chipmunk project on GitHub, created by
;; Jon Morton https://github.com/jmorton
;; 
(defn add-usr-path
  ""
  [& paths]
  (let [field (.getDeclaredField ClassLoader "usr_paths")]
    (try (.setAccessible field true)
         (let [original (vec (.get field nil))
               updated  (distinct (concat original paths))]
           (.set field nil (into-array updated)))
         (finally
           (.setAccessible field false)))))


(defn amend-usr-path
  ""
  [more-paths]
  (apply add-usr-path more-paths))

(defn get-projection
  ([]
   (let [grid_resource (str (:chipmunk-host environ/env) "/grid")
         grid_response (http/get grid_resource)
         json_body (first (json/parse-string (:body @grid_response)))]
     (get json_body "proj")))
  ([local]
   (let [grid (first (file/read-json "resources/grid.conus.json"))]
     (get grid "proj"))))
