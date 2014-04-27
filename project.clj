(defproject bingsearch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [cuma "0.0.8"]
                 [clj-http "0.9.1"]
                 [http-kit "2.1.18"]
                 [org.clojure/data.codec "0.1.0"]
                 [com.github.kyleburton/clj-xpath "1.4.3"]
                 [org.clojure/data.zip "0.1.1"]
                 ]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler bingsearch.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [ring-serve "0.1.2"]
                        ]}})
