(ns bingsearch.handler
  (:use compojure.core)
  (:use bingsearch.view)
  (:use bingsearch.search)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  ;;(GET "/" [] (hello "iiii"))
  (GET "/" [] (index "iiii"))
  (GET "/hello" [] (hello "mikio"))
  (GET "/check" request (check request))
  (GET "/search" request (search request))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
