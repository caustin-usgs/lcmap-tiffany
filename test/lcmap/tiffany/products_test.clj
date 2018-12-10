(ns lcmap.tiffany.products-test
  (:require [clojure.test :refer :all]
            [lcmap.tiffany.products :as products]
            [lcmap.tiffany.file     :as file]
            [lcmap.tiffany.util     :as util]
            [lcmap.tiffany.test-resources :as tr]))

(deftest calc_offset_test
 ; tile pos x pos y -> chip pos x pos y
  (is (= [10 10] (products/calc_offset 1500 1500 1800 1200)))
 ; tile pos x pos y -> chip pos x neg y
  (is (= [10 53] (products/calc_offset 1500 1500 1800 -90)))
 ; -------------------------------------
 ; tile neg x pos y -> chip neg x pos y
  (is (= [10 10] (products/calc_offset -1500 1500 -1200 1200)))
 ; tile neg x pos y -> chip pos x pos y
  (is (= [53 10] (products/calc_offset -1500 1500 90 1200)))
 ; tile neg x pos y -> chip neg x neg y
  (is (= [10 53] (products/calc_offset -1500 1500 -1200 -90)))
 ; tile neg x pos y -> chip pos x neg y
  (is (= [53 53] (products/calc_offset -1500 1500 90 -90)))
 ; ------------------------------------
 ; tile neg x neg y -> chip neg x neg y
  (is (= [10 10] (products/calc_offset -1500 -1500 -1200 -1800)))
 ; tile neg x neg y -> chip pos x neg y
  (is (= [53 10] (products/calc_offset -1500 -1500 90 -1800)))
 ; ------------------------------------
 ; tile pos x neg y -> chip pos x neg y
  (is (= [10 3] (products/calc_offset 1500 -90 1800 -180))))


