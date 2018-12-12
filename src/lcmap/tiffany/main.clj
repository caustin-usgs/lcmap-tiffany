(ns lcmap.tiffany.main
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [mount.core            :as mount]
            [lcmap.tiffany.products   :as products]
            [lcmap.tiffany.gdal       :as gdal]
            [lcmap.tiffany.file       :as file]
            [lcmap.tiffany.util       :as util]))

(defn stderr
  [message]
  (binding [*out* *err*]
    (println message)))

(defn stdout
  [message]
  (println message))

(defmulti generate
  (fn [args]
    (let [arg1 (first args)]
      (keyword arg1))))

(defmethod generate :tile ; "tile" "name" "ulx" "uly" & "files"
  [args]
  (let [params (rest args)
        [_n _ulx _uly] (take 3 params)
        name (str _n)
        ulx (read-string _ulx)
        uly (read-string _uly)
        chips (nthrest params 3)
        projection (util/get-projection "local")]

    (println "generating tile tif based on the following args:")
    (println "name:  " name )
    (println "ulx:   " ulx)
    (println "uly:   " uly)
    (println "chips: " chips)

    (products/create_blank_tile_tiff name ulx uly projection)

    (doseq [i chips]
      (let [input  (file/read-json i)
            values (get input "values")
            chipx  (get input "x")
            chipy  (get input "y")]
        (products/add_chip_to_tile name values ulx uly chipx chipy)))))

(defmethod generate :chip
  [args]
  (let [input_file (second args)
        output_name (last args)]
    (products/generate_product input_file output_name)))

(defmethod generate :default
  [args]
  (stderr (str "invalid request: " args))
  (System/exit 1))

(defn -main
  [& args]
  (mount/start)
  (generate args)
  (System/exit 0))


