(ns lcmap.tiffany.products
  (:require [lcmap.tiffany.file :as file]
            [lcmap.tiffany.gdal :as gdal]
            [lcmap.tiffany.util :as util]))

(defn generate-product
  [input_file output_name]
  (let [input (file/read-json input_file)
        product_values (get input "values")
        chipx (get input "x")
        chipy (get input "y")
        proj_wkt (util/get-projection "local")]
    (gdal/geotiff_from_pixel_array product_values output_name chipx chipy proj_wkt)))

