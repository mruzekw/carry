; Reagent bindings for Carry
(ns carry-reagent.core
  (:require [carry.core :as carry]
            [reagent.core :as r])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn -atom->readonly-ratom
  [a]
  (carry/particle a identity r/atom))

(defn connect
  "Arguments:
  app - the app for which UI should be created
  view-model - function which, given a readonly model ratom, returns reactions; returned value will be passed to a view:
  view - Reagent component function with args: [view-model-return-value dispatch-signal]

  Note that view-model function will be called only once.

  Returns a pair of: view-model value (mostly for testing/debugging) and argless component to be rendered by Reagent."
  [{:keys [model dispatch-signal] :as _app} view-model view]
  {:pre [model (fn? dispatch-signal) (fn? view-model) (fn? view)]}
  (let [app-view-model (view-model (-atom->readonly-ratom model))
        app-view [view app-view-model dispatch-signal]]
    [app-view-model app-view]))

(defn track-keys
  "Returns a map containing reactions to map entries specified by keys."
  [map-ratom keyseq]
  (into {}
        (for [key keyseq]
          [key (reaction (get @map-ratom key))])))