(ns tw2.f.pages.index
  (:require [clojure.string :refer [join]]
            [tw2.c.log :refer [dbg]]
            ["interactjs" :as interact]))

(def places [{:place "london"}
             {:place "paris"}
             {:place "budapest"}])

(defn search []
  )

(defn times [offset]
  [:<> (map (fn [hour] ^{:key hour} [:div hour]) (range 0 24))])

(defn columns []
  [:div#time-grid 
   (for [{:keys [place]} places]
     ^{:key place}
     [:<>
      [:div place]
      [times 0]])])

(defn index-page []
  [columns])

