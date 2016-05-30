(ns app.view-model
  (:require [app.model :as model]
            [carry.core :as carry]
            [counter.core :as counter]
            [reagent.core :as r])
  (:require-macros [reagent.ratom :refer [reaction]]))

#_(defn view-model
    "Naive nonoptimal implementation:
     counter view-models will be updated on every model update ->
      every counter view will be reevaluated on each change."
    [model]
    (let [counter-view-model (fn [id]
                               (counter/view-model
                                 (carry/entangle model #(get-in % [:counters id]) r/atom)))]
      {:counters (reaction (into (sorted-map)
                                 (for [[id _] (:counters @model)]
                                   [id (counter-view-model id)])))}))

(defn view-model
  "Optimized implementation. Reuses counter view-models from the last reaction calculation."
  [model]
  (let [counter-view-models (atom (sorted-map))             ; id -> counter-view-model
        counter-view-model (fn [id]
                             (or (get @counter-view-models id)
                                 (counter/view-model
                                   (carry/entangle model #(get-in % [:counters id]) r/atom))))]
    {:counters (reaction (reset! counter-view-models
                                 (into (sorted-map)
                                       (for [[id _] (:counters @model)]
                                         [id (counter-view-model id)]))))}))