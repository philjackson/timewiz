(ns tw2.f.components.menu
  (:require [reagent.core :as reagent :refer [atom with-let]]
            [reitit.frontend.easy :as rfe]
            ["semantic-ui-react" :as sem]
            [tw2.f.semantic :as s]))

(defn is-active? [{:keys [data]} match-name]
  (= (:name data) match-name))

(defn common-menu-items [match]
  [:<>
   [s/menu-item {:href (rfe/href :index)
                 :active (is-active? match :index)}
    "Home"]])

(defn identity-menu-items [match]
  [:<>])

(defn mobile-menu [match child]
  (with-let [visible (atom false)]
    [s/sidebar-pushable 
     [s/sidebar {:as sem/Menu
                 :visible @visible
                 :on-hide #(reset! visible false)
                 :icon "labeled"
                 :animation "overlay"
                 :on-click #(reset! visible false)
                 :width "thin"
                 :vertical true}
      [common-menu-items match]
      [identity-menu-items match]]
     
     [s/sidebar-pusher {:dimmed @visible}
      [s/menu
       [s/menu-item {:icon "bars" :on-click #(reset! visible (not @visible))}]
       [s/menu-menu {:position "right"}
        [s/menu-item {:header true} "timewiz"]]]
      [:div {:style {:min-height "100vh"}}
       child]]]))

(defn desktop-menu [match child]
  [:<>
   [s/menu {:id "desktop-menu"
            :pointing true
            :color "orange"
            :borderless true}
    [s/menu-item {:header true}
     (with-let [visible (atom true)]
       [s/transition {:animation "tada"
                      :visible @visible
                      :duration 800}
        [:a {:href (rfe/href :index)
             :on-click #(reset! visible (not @visible))}
         [s/menu-item {:header true} "timewiz"]]])]
    [common-menu-items match]
    [s/menu-menu {:position "right"}
     [identity-menu-items match]]]
   child])

(defn menu [match content]
  [:div {:style {:min-height "100vh"}}
   [s/responsive s/only-mobile [mobile-menu match content]]
   [s/responsive s/above-mobile [desktop-menu match content]]])
