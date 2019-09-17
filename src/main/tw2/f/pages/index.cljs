(ns tw2.f.pages.index
  (:require [clojure.string :refer [join]]
            [reagent.core :refer [atom with-let]]
            [tw2.c.log :refer [dbg-str dbg]]
            [ajax.core :refer [GET]]
            ["interactjs" :as interact]
            ["flexsearch" :as FlexSearch]
            [clojure.string :refer [split]]
            [tw2.f.semantic :as s]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]))

(def places (atom [{:city "London" :region "Europe/London"}
                   {:city "St Johns" :region "America/St_Johns"}
                   {:city "Budapest" :region "Europe/Budapest"}]))

(def idx (FlexSearch. #js {:encode "icase"
                           :tokenize "forward"}))

(GET "/index.index" {:handler (fn [all]
                                (.import idx all))})

(defn search []
  (with-let [results (atom nil)]
    [s/search {:input {:fluid true}
               :fluid true
               :results @results
               :on-result-select
               (fn [_ results]
                 (let [res (js->clj (.-result results) :keywordize-keys true)
                       {:keys [title description]} res]
                   (swap! places conj {:city title :region description})))

               :on-search-change
               (fn [_ input]
                 (let [val (:value (js->clj input :keywordize-keys true))]
                   (.search idx val
                            #js {:limit 20 :threshold 2}
                            (fn [hits]
                              (reset! results
                                      (map (fn [hit]
                                             (let [[region city] (split hit #"\.{3}")]
                                               {:title city
                                                :description region
                                                :key hit}))
                                           hits))))))
               :placeholder "Find a city or a timezone"}]))

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
   ^{:key (str (:city place) i)}
   [:div.cell (format-time (t/plus start (t/hours i)) :12 offset)])])

(defn extract-current-tz [timestamps]
  (first (filter #(or (< (t/epoch) (first %))
                      (nil? (first %))) timestamps)))

(def fetches (atom {}))
(defn column [place]
  (with-let [now (t/now)
             now-l (tc/to-long now)]
    (let [[_ short-name offset] (get @fetches (:region place))]
      (if-not short-name
        (do
          ;; fill the atom with the tz details
          (GET (str "/tz/" (:region place) ".json")
              {:handler #(swap! fetches assoc (:region place)
                                ;; extract the relevant timezone
                                ;; offset
                                (extract-current-tz %))})
          [:div "Loading."])
        [s/segment {:class "column"}
         [:div.cell-header
          [:b (:city place)]
          [:div short-name]]
         [times (t/minus now (t/minutes offset)) place offset]]))))

(defn columns []
  [:div#time-grid
   (for [place @places]
     ^{:key (:city place)} [column place])])

(defn index-page []
  [:<>
   [search]
   [columns]])
