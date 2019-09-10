(ns tw2.f.routes
  (:require [tw2.f.pages.index :refer [index-page]]))

(def routes [["/" {:name :index :view index-page}]])
