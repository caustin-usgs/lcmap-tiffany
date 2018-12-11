(ns lcmap.tiffany.products
  (:require [lcmap.tiffany.file :as file]
            [lcmap.tiffany.gdal :as gdal]
            [lcmap.tiffany.util :as util]
            [clojure.math.numeric-tower :as math]))

;; a tile is 50 chips x 50 chips
;; each chip is 100 pixels x 100 pixels
;; each pixel is 30m x 30m
(def meters_per_pixel     30)
(def pixels_per_chip_side 100)
(def chips_per_tile_side  50)

(defn generate-product
  [infile name]
  (let [input (file/read-json infile)
        values (get input "values")
        chipx (get input "x")
        chipy (get input "y")
        projection (util/get-projection "local")]
    (gdal/create_chip_tiff name values chipx chipy projection)))

(defn calc_offset
  "Returns pixel offset for UL coordinates of chips"
  [tile_x tile_y chip_x chip_y]
  (let [x_diff (- chip_x tile_x)
        y_diff (- tile_y chip_y)
        x_offset (/ x_diff meters_per_pixel)
        y_offset (/ y_diff meters_per_pixel)]
    [x_offset y_offset]))




