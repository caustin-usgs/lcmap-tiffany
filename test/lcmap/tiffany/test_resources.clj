(ns lcmap.tiffany.test-resources
  (:require [lcmap.tiffany.file :as file]
            [lcmap.tiffany.util :as util]))

(def chip_data (file/read-json "resources/y3161805_x-2115585_nodates.json"))
(def pixel_segments (util/pixel-groups chip_data))
(def grid_data (file/read-json "resources/grid.json"))
