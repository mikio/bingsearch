(ns bingsearch.view
  (:require [cuma.core :refer [render]])
  (:use bingsearch.search)
  )

(defn- my-render [fname data]
  (render (slurp (str "template/" fname)) data))

(defn hello [name]
  (my-render "hello.html" {:name name}))

(defn index [name]
  (println "test")
  (my-render "search.html" {:name name}))

(defn search [request]
  (println "search")
  (println request)
  (my-render "search.html" (search-result request)))

(defn check [request]
  (println "check")
  (println request)
  (my-render "hello.html" (:params request)))
