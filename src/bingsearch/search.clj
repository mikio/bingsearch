(ns bingsearch.search
  ;;(:require [clj-http.client :as client])
  (:require [org.httpkit.client :as http])
  (:require [ring.util.codec :as codec])
  (:require [clojure.data.codec.base64 :as b64])
  (:require [clojure.xml :as xml])
  (:require [clojure.zip :as zip])
  (:require [clojure.data.zip.xml :as zipx])
  )
(def acct-key "9zjbFceWN5ptcMBEaD35Vz2Vh1Q2qx7cPNu980+aoJ8")
;;(def root-uri "https://api.datamarket.azure.com/Bing/Search")
;;(def root-uri "api.datamarket.azure.com/Bing/Search")
(def root-uri "https://api.datamarket.azure.com/Bing/Search/Composite?Sources=%27Web%2BNews%27")
(def pager-width 10)

(defn xml-parse [s]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes s))))

(defn xml-zip [xml-map]
  (zip/xml-zip xml-map))

(defn request-uri [query fr dc]
  (let [query4url (codec/url-encode query)]
    (str root-uri "&Query=%27" query4url "%27&$skip=" fr "&$top=" dc)))

;; (defn b64-encode [s]
;;   (String. (b64/encode (.getBytes s)) "UTF-8")
;;   )
;; (defn request-uri [query fr dc]
;;   (let [pass (str acct-key ":" acct-key)]
;;     (str "https://" pass "@" root-uri "/Web?Query=%27" query "%27&$skip=" fr "&$top=" dc)))

;; (defn fetch [uri]
;;   (:body (client/get uri {:basic-auth [acct-key acct-key]}))
;; ;;  (slurp uri)
;;   )

(defn fetch [uri]
  (let [options {:basic-auth [acct-key acct-key]}
        result @(http/get uri options)]
    (:body result)))

(defn str->int [s defo]
  (if (or (= "" s) (nil? s)) defo (Integer/parseInt s)))

(defn str->comma [n]
  (if (number? n) (format "%1$,3d" n) ""))

(defn search-link [query fr dc]
  (let [query4url (codec/url-encode query)]
    (str "/search?query=" query4url "&fr=" fr "&dc=" dc)))

(defn frs
  "pagerに使用するためのfrパラメータを計算する"
  [start end dc]
  ;;(range start (+ start (* width dc)) dc)
  (range start end dc)
  )

(defn urls [frs fr query dc]
  (for [f frs] (if (= f fr) nil (search-link query f dc))))

(defn pager [query fr dc width total]
  (let [center (/ width 2)
        max (/ total dc)
        pos (/ (+ fr dc) dc)
        spos (- pos center)
        spos (if (< spos 0) 0 spos)
        epos (+ spos width)
        epos (if (> epos max) max epos)
        frs (frs (* spos dc) (* epos dc) dc)
        titles (map #(/ (+ % dc) dc) frs)
        urls (urls frs fr query dc)
        ]
    (vec (map #(hash-map :title %1 :url %2) titles urls))))

(defn search-result [request]
  (let [query (:query (:params request))
        fr (:fr (:params request))
        dc (:dc (:params request))
        nfr (str->int fr 0)
        ndc (str->int dc 10)
        nto (+ nfr ndc)
        str-xml (fetch (request-uri query nfr ndc))
        results (xml-zip (xml-parse str-xml))
        titles (zipx/xml-> results :entry :link :m:inline :feed :entry :content :m:properties :d:Title zipx/text)
        descs (zipx/xml-> results  :entry :link :m:inline :feed :entry :content :m:properties :d:Description zipx/text)
        durls (zipx/xml-> results  :entry :link :m:inline :feed :entry :content :m:properties :d:DisplayUrl zipx/text)
        urls (zipx/xml-> results   :entry :link :m:inline :feed :entry :content :m:properties :d:Url zipx/text)
        ;;result {:results (vec (for [title titles] {:title title}))}
        total (first (zipx/xml-> results :entry :content :m:properties :d:WebTotal zipx/text))
        ntotal (str->int total 0)
        nto (if (< ntotal nto) ntotal nto)
        result {:query (str query)
                :fr (str (+ nfr 1))
                :to (str nto)
                :total (str->comma ntotal)
                :pager (pager query nfr ndc pager-width ntotal)
                :results (vec (map #(hash-map :title %1 :desc %2 :durl %3 :url %4)
                                   titles descs durls urls))}]
    (prn result)
    result))


