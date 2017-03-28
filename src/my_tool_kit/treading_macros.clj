(ns my-tool-kit.treading-macros)

(defn transform [person]
  (update (assoc person :hair-color :gray) :age inc))

(transform {:name "Socrates", :age 39})

;; by utilizing the thread-first macro
;; the above codes could be rewritten

(defn transform* [person]
  (-> person
      (assoc :hair-color :gray) ;; person being the first argument
      (update :age inc))) ;; value from above used as the first argument


(transform* {:name "Socrates", :age 39})

(let [person (transform* {:name "Socrates", :age 39})]
  (->
   person
   :hair-color
   name
   clojure.string/upper-case
   println)
  (->
   person
   (:hair-color)
   (name)
   (clojure.string/upper-case)
   (println)))
;; GRAY
;; GRAY

;; thread-last macros
;; the previous value is passed
;; into the last argument of the next method
(defn calculate* []
  (->> (range 10)
       (filter odd? ,,,)
       (map #(* % %) ,,,)
       (reduce + ,,,)))

(calculate*)

(->
 "pref-thadd-dd"
 clojure.string/lower-case
 (.startsWith "prefix"))

;; as-> is more flexible than -> and ->>
;; in that the binding v could be used at
;; any position in the subsequent calls.
(as-> [:foo :bar] v
  (map name v)
  (first v)
  (.substring v 1))

(when-let [counter (:counter {:counter "123"})]
  (inc (Long/parseLong counter)))

;; Similar to ->
;; the diff being once nil is counter
;; the pipeline stops and return nil
(some->
 {:counter-error "123"}
 :counter
 Long/parseLong
 inc)

;;;;;;;;;;;;;;;; FIXME
;;;;;;;;;;;;;;;; add con->
