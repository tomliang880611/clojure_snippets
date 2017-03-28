(ns my-tool-kit.misc)

;;;;;;;;;;;;;;;; var binding
(def ^:dynamic x 1)
(def ^:dynamic y 1)
(binding [x 3 y 4]
  (+ x y))
;; 7 as the result

;;;;;;;;;;;;;;;; refs and transaction
(defn run [nvecs nitems nthreads niters]
  (let [vec-refs (vec (map (comp ref vec)
                           (partition nitems (range (* nvecs nitems)))))
        swap #(let [v1 (rand-int nvecs)
                    v2 (rand-int nvecs)
                    i1 (rand-int nitems)
                    i2 (rand-int nitems)]
                (dosync
                 (let [temp (nth @(vec-refs v1) i1)]
                   (alter (vec-refs v1) assoc i1 (nth @(vec-refs v2) i2))
                   (alter (vec-refs v2) assoc i2 temp))))
        report #(do
                  (prn (map deref vec-refs))
                  (println "Distinct:"
                           (->>
                            vec-refs
                            (map deref ,,,)
                            (apply concat ,,,)
                            (distinct ,,,)
                            (count ,,,))))]
    (report)
    (dorun (apply pcalls (repeat nthreads #(dotimes [_ niters] (swap)))))
    (report)))

(run 100 10 10 1000)

;;;;;;;;;;;;;;;; distinct
(distinct [1 2 3 4 5 6 2 3 4])

;;;;;;;;;;;;;;;; macro
(macroexpand '(-> {} (assoc :a 1) (assoc :b 2)))
(-> {} (assoc :a 1) (assoc :b 2))

;;;;;;;;;;;;;;;; functions ;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;  map
(map (fn [x] (+ 2 x)) [1 2 3])
(map #(+ 2 %) [1 2 3])
;;;;;;;;;;;;;;;; partial
(map (partial + 2) [1 2 3])

;;;;;;;;;;;;;;;;  comp
(map (comp - *) [2 4 6] [1 2 3])
(map (comp - *) [2 4 6] [1 2])
(map (comp - *) [2 4 6] [1])
(map (comp - *) [1] [2] [3] [4] [5])

;; (- (* 1 2 3))
;; from right to left
((comp - *) 1 2 3)
(- 6)

;;;;;;;;;;;;;;;; constantly
(map (constantly 9) [1 2 3])

;;;;;;;;;;;;;;;; complement
(map (complement zero?) [3 2 1 0])

;;;;;;;;;;;;;;;; regex support
(re-seq #"[0-9]+" "abs123ef34ghi567")
(re-find #"([-+]?[0-9]+)/([0-9]+)" "22/7")
(re-matches #"([-+]?[0-9]+)/([0-9]+)" "22/7")
(re-seq #"(?i)[fq].." "foo bar BAZ QUX quux")

;;;;;;;;;;;;;;;; strings
(map (fn [x] (.toUpperCase x)) (.split "this is a test case..." " "))


;;;;;;;;;;;;;;;; data structure
(== 1 1.0 1M)
(/ 2 3)
(/ 2.0 3)
(map #(Math/abs %) (range -3 3))


(defn hash-ordered [collection]
  (->
   (reduce (fn [acc e] (unchecked-add-int
                        (unchecked-multiply-int 31 acc)
                        (hash e)))
           collection)
   (mix-collection-hash (count collection))))

(defn hash-unordered [collection]
  (-> (reduce unchecked-add-int 0 (map hash collection))
      (mix-collection-hash (count collection))))

(reduce #(+ %1 %2) (range 1 10))
(reduce #(+ %1 %2) 0 [1])

(defstruct desilu :fred :ricky)
(def x (map
        (fn [n]
          (struct-map desilu
                      :fred n
                      :ricky 2
                      :lucy 3
                      :ethel 4))
        (range 1000)))

(def fred (accessor desilu :fred))
(reduce (fn [n y] (+ n (:fred y))) 0 x)
(reduce (fn [n y] (+ n (fred y))) 0 x)

#{:a :b :c :d :e}
(sorted-set :a :b :c)

(def s (set [1 2 3 4 5 1 2 3]))
(conj s 1)

(contains? #{:a :b :c} :a)

;; equivalent to `(get-in {:a 0 :b {:c "ho hum"}} [:b :c])'
(-> {:a 0 :b {:c "ho hum"}}
    :b
    :c)
;;; return true when found nil
(contains? #{:a :b :c nil} nil)

(into [] (set [:a :b :c :b]))
