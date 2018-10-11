(ns lcmap.tiffany.main
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [mount.core            :as mount]
            [lcmap.tiffany.products   :as products]
            [lcmap.tiffany.gdal       :as gdal]))

;; ccdc results are the number of change segments
;; detected over a 100x100 pixel area (chip)
;; There can be multiple changes over time 
;; detected per pixel.
;; if no changes are detected, a result is 
;; returned with an sday and eday == 0

(defn -main
  [input_file output_name]
  ;; Only mount states defined in required namespaces are started.
  (mount/start)
  (products/generate-product input_file output_name)
  (System/exit 0))


