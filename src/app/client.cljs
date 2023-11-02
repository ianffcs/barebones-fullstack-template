(ns app.client
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))


(defn my-component []
  (let [init-config {:counter 0}]
    (r/with-let [state (r/atom init-config)]
      [:<>
       [:input {:type     "button"
                :value    (-> @state
                              :counter)
                :on-click #(swap! state
                                  (fn [st]
                                    (-> st
                                        (update :counter inc))))}]
       [:div {:style {"color" "#002aff"}}
        "AAAAAAAA"]])))

(defn ^:dev/after-load -main []
  (rd/render
    [my-component]
    (js/document.getElementById "app")))