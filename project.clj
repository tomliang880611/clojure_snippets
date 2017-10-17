(defproject my-tool-kit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [taoensso.com/timbre "0.5.1-SNAPSHOT"]]
  :main ^:skip-aot my-tool-kit.core
  :resource-paths ["lib/*"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
