(ns my-tool-kit.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(do
  (println "first one line")
  (println "yet another line..."))

; normal destruction
(def my-list '(1 2 3))
(let [[a b c] my-list]
  (println a)
  (println b)
  (println c))

; destruction with remaining
(let [[a & remaining] my-list]
  (println a)
  (println remaining))

; destruction with some item ignored
(def names ["Michael" "Amber" "Aaron" "Nick" "Earl" "Joe"])
(let [[item1 _ item3 _ item5 _] names]
  (println "Odd names:" item1 item3 item5))

; :as could be used to bind the seq
(let [[item1 :as all] names]
  (println "The first name from" all "is" item1))

(let [[item1 :as all-items] names]
  (println "The first name from" all-items "is" item1))

(def fruits ["apple" "orange" "strawberry" "peach" "pear" "lemon"])
(let [[item1 _ item3 & remaining :as all-fruits] fruits]
  (println "The first and third fruits are" item1 "and" item3)
  (println "These were taken from" all-fruits)
  (println "The fruits after them are" remaining)
  "hahaha")

(def my-line [[5 10] [10 20]])
(let [[[a b :as group1] [c d :as group2]] my-line]
  (println a b group1)
  (println c d group2))

;;;; associative deconstruction
(def client {:name "Super Co."
             :location "Philadelphia"
             :description "The worldwide leader in plastic tableware."})

(let [name (:name client)
      location (:location client)
      description (:description client)]
  (println name location description))

(let [{name :name
       location :location
       description :description} client]
  (println description location name))

(let [{:keys [name location description]} client]
  (println name location "-" description))

(let [{name :name :as all} client]
  (println "The name from" all "is" name))

(def my-map {:a "A" :b "B" :c 3 :d 4})
(let [{a :a, x :x, :or {x "Not found!"}, :as all} my-map]
  (println "I got" a "from" all)
  (println "Where is x?" x))

(def multiplayer-game-state
  {:joe {:class "Ranger"
         :weapon "Longbow"
         :score 100}
   :jane {:class "Knight"
          :weapon "Greatsword"
          :score 140}
   :ryan {:class "Wizard"
          :weapon "Mystic Staff"
          :score 150}})

(let [{{:keys [class weapon]} :joe} multiplayer-game-state]
  (println "Joe is a" class "wielding a" weapon))

;; key word arguments
(defn configure [val options]
  (let [{:keys [debug verbose] :or {debug false, verbose false}} options]
    (println "val = " val " debug = " debug " verbose " verbose)))

(configure 12 {:debug true})
(configure 13 {:verbose true})
;; val =  13  debug =  false  verbose  true

(defn configure-2nd [val &
                     {:keys [debug verbose]
                      :or {debug false verbose false}}]
  (println "val = " val " debug = " debug " verbose = " verbose))

(configure-2nd 10)
(configure 13 {:verbose true})
(configure-2nd 15 :debug true :verbose true)

;; namespaced keyword
(def human {:person/name "Franklin"
            :person/age 25
            :hobby/hobbies "running"})

(let [{:keys [:person/name :person/age :hobby/hobbies]} human]
  (println name "is" age "and likes" hobbies))

(defn f-with-options
  [a b & {:keys [option]}]
  (println "Got" a b option))

(f-with-options "abc" "cdf" :option true)

(defn print-coordinate [point]
  (let [[x y z] point]
    (println x y z)))

(print-coordinate [1 2 3])

(defn print-coordinate-2 [[x y z]]
  (println x y z))

(print-coordinate-2 [4 5 6])

(defn print-contact-info [{:keys [f-name l-name phone company title]}]
  (println f-name l-name "is the" title "at" company)
  (println "You can reach him at" phone))

(print-contact-info {:f-name "John"
                     :l-name "Smith"
                     :phone "555-555-555"
                     :company "Functional Industries"
                     :title "Sith Lord of Git"})

(def john-smith {:f-name "John"
                 :l-name "Smith"
                 :phone "555-555-555"
                 :company "Functional Industries"
                 :address {:street "452 Lisp In."
                           :city "Macroville"
                           :state "Kentucky"
                           :zip "81321"}
                 :hobbies ["running" "hiking" "basketball"]
                 :title "Sith Lord of Git"})

;;;; perfect example of deconstruction
(defn print-contact-info-2
  [{:keys [f-name l-name phone company title]
    {:keys [street city state zip]} :address
    [fav-hobby second-hobby] :hobbies}]
  (println f-name l-name "is the" title "at" company)
  (println "You can reach him at" phone)
  (println "He lives at" street city zip)
  (println "Maybe you can write to him about" fav-hobby "or" second-hobby))

(print-contact-info-2 john-smith)

;; macros
(destructure '[[x & remaining :as all] numbers])
;; => [vec__20405 numbers x (clojure.core/nth vec__20405 0 nil) remaining (clojure.core/nthnext vec__20405 1) all vec__20405]

