(ns tw2.f.pages.index
  (:require [clojure.string :refer [join]]
            [reagent.core :refer [atom with-let]]
            [tw2.c.log :refer [dbg-str dbg]]
            [ajax.core :refer [GET]]
            ["interactjs" :as interact]
            ["lunr" :as lunr]
            [tw2.f.semantic :as s]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]))

(def places [{:place "London" :tz "Europe/London"}
             {:place "St Johns" :tz "America/St_Johns"}
             {:place "Budapest" :tz "Europe/Budapest"}])

(defn munge-indexes [indexer regions]
  (doall
   (for [[region cities] regions
         city cities]
     (.add indexer (dbg (clj->js {:city city
                                  :region region
                                  :id city}))))))

(def idx (lunr (fn []
                 (this-as this
                   (.field this "city")
                   (.field this "region")
                   (.ref this "id")
                   (munge-indexes this (dbg (GET "/index.json")))))))

(defn search []
  [s/search {:input {:fluid true}
             :fluid true
             :on-search-change (fn [_ input]
                                 (let [val (:value (js->clj input :keywordize-keys true))]
                                   (dbg (.search idx val))))
             :placeholder "Find a city or a timezone"}])

(defn format-time [time fmt offset]
  (let [stupid-offset (rem offset 60)]
    (cond
      (= fmt :12) [:div.time
                   [:div.hours (tf/unparse
                                (tf/formatter "h")
                                time)
                    (when-not (= stupid-offset 0)
                      [:span.extra-mins ":" stupid-offset])]

                   [:div.meridiem (tf/unparse
                                   (tf/formatter "a")
                                   time)]]
      (= fmt :24) [:div.time
                   [:span.hours (tf/unparse
                                 (tf/formatter "H")
                                 time)]])))

(defn times [start place offset]
  [:<>
   (for [i (range 0 24)]
     ^{:key (str (:place place) i)}
     [:div.cell (format-time (t/plus start (t/hours i)) :12 offset)])])

(defn extract-current-tz [timestamps]
  (first (filter #(or (< (t/epoch) (first %))
                      (nil? (first %))) timestamps)))

(def fetches (atom {}))
(defn column [place]
  (with-let [now (t/now)
             now-l (tc/to-long now)]
    (let [[_ short-name offset] (get @fetches (:tz place))]
      (if-not short-name
        (do
          ;; fill the atom with the tz details
          (GET (str "/tz/" (:tz place) ".json")
              {:handler #(swap! fetches assoc (:tz place)
                                ;; extract the relevant timezone
                                ;; offset
                                (extract-current-tz %))})
          [:div "Loading."])
        [s/segment {:class "column"}
         [:div.cell-header
          [:b (:place place)]
          [:div short-name]]
         [times (t/minus now (t/minutes offset)) place offset]]))))

(defn columns []
  [:div#time-grid
   (for [place places]
     ^{:key (:place place)} [column place])])

(defn index-page []
  [:<>
   [search]
   [columns]])
