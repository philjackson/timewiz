(ns tw2.f.pages.index
  (:require [clojure.string :refer [join]]))

(def places [{:place "london"}
             {:place "paris"}
             {:place "budapest"}])

(defn search []
  )

(defn times []
  [:<> (map (fn [hour] [:div hour]) (range 0 24))])

(defn columns []
  [:div#time-grid 
   (for [{:keys [place]} places]
     ^{:key place}
     [:<>
      [:div place]
      [times]])])

(defn index-page []
  [columns])

