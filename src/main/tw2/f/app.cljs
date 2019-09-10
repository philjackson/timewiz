(ns tw2.f.app
  (:require [reagent.core :as reagent]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend :as rf]
            [reitit.coercion.spec :as rss]
            [tw2.c.log :refer [dbg]]
            [tw2.f.components.menu :refer [menu]]
            [tw2.f.routes :refer [routes]]))

;; store the match in an atom so it becomes reactive
(defonce match (reagent/atom nil))

(defn current-page []
  (when-let [route-match  @match]
    ;; an initial div which has the class name set to the route match
    ;; name
    [:div {:class (name (:name (:data @match)))}
     ;; menu needs to take children directly thanks to the odd pusher
     ;; stuff on mobile
     [menu route-match
      [:div#main
       [(:view (:data @match)) @match]]]]))

(defn stop [])

(defn start []
  (dbg "Starting...")
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   #(reset! match %)
   {:use-fragment true})
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init []
  (dbg "Initiating...")
  (start))
