(ns lcmap.tiffany.gdal
  (:require [mount.core :as mount]
            [lcmap.tiffany.util :as util])
  (:import [org.gdal.gdal gdal]
           [org.gdal.gdal Driver]
           [org.gdal.gdal Dataset]
           [org.gdal.gdalconst gdalconst]))

;; init and state constructs blatantly ripped off from the
;; USGS-EROS/lcmap-chipmunk project on GitHub, created by
;; Jon Morton https://github.com/jmorton
;; 
;; ## Init
;;
;; This makes it easier to use Java GDAL libraries without
;; having to set environment variables. These are typical
;; install locations of GDAL libs on CentOS and Ubuntu.
;;
;; Before GDAL can open files, drivers must be registered.
;; Selective registration is more tedious and error prone,
;; so we just register all drivers.
;;
;; If anything goes wrong, a helpful string is printed to
;; stdout (not a log file).
;;

(defn init
  "Initialize GDAL drivers."
  []
  (try
    (util/amend-usr-path ["/usr/lib/java/gdal" "/usr/lib/jni"])
    (gdal/AllRegister)
    (catch RuntimeException e
      (binding [*out* *err*]
        (println (str "Could not update paths to native libraries. "
                      "You may need to set LD_LIBRARY_PATH to the "
                      "directory containing libgdaljni.so"))))
    (finally
      (import org.gdal.gdal.gdal))))


;; ## State
;;
;; A mount state is defined so that GDAL is initialized like
;; everything else (DB connections, HTTP listeners, etc...)
;;

(mount/defstate gdal-init
  :start (init))

(defn dataset_and_band
  [output_name x_dim y_dim]
  (let [tiff_driver  (gdal/GetDriverByName "GTiff")
        tiff_dataset (.Create tiff_driver output_name x_dim y_dim)
        tiff_band    (.GetRasterBand tiff_dataset 1)]
    [tiff_dataset tiff_band]))

(defn write_data
  [tiff_dataset tiff_band x_offset y_offset x_dim y_dim values]
  (.WriteRaster tiff_band x_offset y_offset x_dim y_dim (float-array values))
  (.delete tiff_band)
  (.delete tiff_dataset))

(defn set_transform_and_proj
  [ulx uly tiff_dataset proj_wkt]
  (let [transform (double-array [ulx 30 0 uly 0 -30])] ;(XULCorner,Cellsize,0,YULCorner,0,-Cellsize)
    (.SetGeoTransform tiff_dataset transform)
    (.SetProjection tiff_dataset proj_wkt)))

(defn geotiff_from_pixel_array
  [pixel_array output_name ulx uly proj_wkt]
  (let [[tiff_dataset tiff_band] (dataset_and_band output_name 100 100)]
    (set_transform_and_proj ulx uly tiff_dataset proj_wkt)
    (write_data tiff_dataset tiff_band 0 0 100 100 pixel_array))
  output_name)

(defn create_tile_tiff
  [tile_name ulx uly proj_wkt]
  (let [[tiff_dataset tiff_band] (dataset_and_band tile_name 5000 5000)]
    (set_transform_and_proj ulx uly tiff_dataset proj_wkt)
    (write_data tiff_dataset tiff_band 0 0 5000 5000 (repeat (* 5000 5000) 0))))

(defn add_chip_to_tile
  ([tile_tiff_path chip_values x_offset y_offset x_size y_size]
   (let [tile_dataset (gdal/Open tile_tiff_path 1)
         tile_band (.GetRasterBand tile_dataset 1)]
     (.WriteRaster tile_band x_offset y_offset x_size y_size (float-array chip_values))
     (.delete tile_band)
     (.delete tile_dataset)))
  ([tile_tiff_path chip_values x_offset y_offset]
   (add_chip_to_tile tile_tiff_path chip_values x_offset y_offset 100 100)))


