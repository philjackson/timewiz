#!/usr/bin/env lumo

(ns index-builder.indexer
  (:require [cljs.nodejs :as node]
            [clojure.string :as string]))

(defonce FlexSearch (js/require "flexsearch"))
(defonce https (node/require "https"))
(defonce fs (node/require "fs"))

(defn build-index [lines]
  "Build a structure representing a suitable index file."
  (reduce (fn [cur line]
            (let [[main city] (string/split line ",")]
              (update cur main conj city)))
          {}
          lines))

(defn to-json [index]
  "Convert clojure to json."
  (.stringify js/JSON (clj->js index)))

(def idx (FlexSearch. #js {:encode "icase"
                           :tokenize "strict"}))

(defn munge-indexes [regions]
  (doseq [[region cities] regions
          city cities]
    (.add idx (str region "..." city) city)))

(let [lines (string/split (.readFileSync fs "cities15000.txt") "\n")
      index (build-index lines)]
  ;; read in the cities file and convert to json where continent points
  ;; to various cities. Writes out the file once it's done.
  (.writeFileSync fs "index.json" (to-json index))

  ;; also output a flexsearch index file
  (munge-indexes index)
  (.writeFileSync fs "index.index" (.export idx)))


