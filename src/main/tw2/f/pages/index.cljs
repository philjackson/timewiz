(ns tw2.f.pages.index
  (:require [clojure.string :refer [join]]
            [reagent.core :refer [atom with-let create-class]]
            [alandipert.storage-atom :refer [local-storage]]
            [ajax.core :refer [GET]]
            ["interactjs" :as interact]
            ["flexsearch" :as FlexSearch]
            [clojure.string :refer [split]]
            [tw2.f.semantic :as s]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]))

(def places (local-storage
             (atom [{:city "London" :region "Europe/London"}
                    {:city "St Johns" :region "America/St_Johns"}
                    {:city "Budapest" :region "Europe/Budapest"}])
             :places))

(def idx (FlexSearch. #js {:encode "icase"
                           :tokenize "forward"}))

(defn already-have-place? [new-place]
  (boolean (first (filter @places #(= % new-place)))))

(defn search []
  (let [results (atom nil)
        value (atom "")
        search-loaded? (atom false)]
    (create-class
     {:display-name "index-page"

      :component-will-mount
      (fn [] (GET "/index.index"
                 {:handler (fn [all]
                             (.import idx all)
                             (reset! search-loaded? true))}))

      :reagent-render
      (fn []
        [s/search {:input {:fluid true}
                   :fluid true
                   :value @value
                   :results @results

                   :on-result-select
                   (fn [_ results]
                     (let [res (js->clj (.-result results) :keywordize-keys true)
                           {:keys [title description]} res]
                       (swap! places conj {:city title :region description})
                       (reset! value "")))

                   :on-search-change
                   (fn [_ input]
                     (reset! value (.-value input))
                     (.search idx @value
                              #js {:limit 20 :threshold 2}
                              (fn [hits]
                                (reset! results
                                        (map (fn [hit]
                                               (let [[region city] (split hit #"\.{3}")]
                                                 {:title city
                                                  :description region
                                                  :key hit}))
                                             hits)))))
                   :placeholder "Find a city or a timezone"}])})))

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
     (let [time (t/plus start (t/hours i))
           hour (t/hour time)]
       ^{:key (str (:city place) i)}
       [:div.cell {:class (cond
                            (= hour 0)
                            ["terrible-time" "start-of-day"]

                            (= hour 23)
                            ["terrible-time" "end-of-day"]

                            ;; inconvenient hours
                            (or (and (>= hour 6)
                                     (<= hour 8))
                                (and (>= hour 18)
                                     (<= hour 20)))
                            "bad-time"
                            
                            ;; no way hours
                            (or (< hour 6)
                                (> hour 20))
                            "terrible-time"

                            :else
                            "good-time")}
        (format-time time :12 offset)]))])

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
        [:<>
         [:div.drag-bar "::::"]
         [:div.cell-header {:title (str (:city place) " - " (:region place))}
          [:b.city (:city place)]
          [:div short-name]]
         [times (t/minus now (t/minutes offset)) place offset]]))))

(defn columns []
  [:div#time-grid
   (for [place @places]
     ^{:key (str place)} [column place])])

(defn index-page []
  [:<>
   [search]
   [columns]])
