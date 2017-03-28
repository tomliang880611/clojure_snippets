(ns my-tool-kit.xml-manipulation
  ;; (:import [clojure.lang IPersistentList IPersistentMap IPersistentVector ISeq])
  (:require [my-tool-kit.tree-visitor :refer [tree-zipper tree-visitor]])
  (:require [clojure.xml :refer [parse tag attrs content]])
  (:require [clojure.zip :refer [zipper xml-zip down right rights end? edit] :as zip]))

(def test-xml-path "/Users/tom-liang/project/iKCoder.WebApplications/iKCoder/demos/computer-game/index.html")

(reduce #(if (= (tag %2) :script) (->> (attrs %2) :src (conj %1)) %1) []
        (->
         (parse test-xml-path)
         xml-zip ;; root
         down ;; head
         right ;; body
         down ;; first script
         rights)) ;; rest at the same level

(->>
 (parse test-xml-path)
 xml-zip ;; root
 down ;; head
 right ;; body
 down ;; first script
 rights
 (reduce #(if (= (tag %2) :script) (->> (attrs %2) :src (conj %1)) %1) [])) ;; rest at the same level

(->>
 (parse test-xml-path)
 xml-zip ;; root
 down ;; head
 right ;; body
 down ;; first script
 rights
 (reduce #(if (= (tag %2) :script) (->> (attrs %2) :src (conj %1)) %1) [])) ;; rest at the same level


(->>
 (parse test-xml-path)
 xml-zip ;; root
 down ;; head
 right ;; body
 down ;; first script
 rights
 first
 xml-zip
 down
 first
 :content
 prn) ;; rest at the same level

(->
 (parse test-xml-path)
 xml-zip
 down
 right
 down
 rights)


;; Note the use of `partial
;; The partial function partially applies a function with a subset of its arguments and returns a new one that takes fewer arguments.
;; `partial could be use to adapt into a slot that demands for a function with different number of arguments
(defn tree-edit [zipper matcher editor]
  (loop [loc zipper]
    (if (end? loc)
      (zip/root loc)
      (if-let [matcher-result (matcher (zip/node loc))]
        (recur (zip/next (edit loc (partial editor matcher-result))))
        (recur (zip/next loc))))))

(defn can-simplify-concat [node]
  (and (map? node)
       (= :script (tag node))
       (every? string? (:script val))))

(defn simplify-concat [_ node]
  (clojure.string/join (:script node)))

(defn simplify-concat-zip [node]
  (tree-edit (xml-zip node)
             can-simplify-concat
             simplify-concat))

(->
 (simplify-concat-zip (parse test-xml-path))
 clojure.pprint/pprint)
;;;;;;;;;;;;;;;; end tree-edit

(defn string-visitor
  [node state]
  (when (string? node)
    {:state (conj state (clojure.string/upper-case node))}))

(defn string-finder [node]
  (:state
   (tree-visitor (tree-zipper node) #{} [string-visitor])))

(string-finder [1 "string" "string2" "string3"])

(->>
 (tree-zipper [1 ["string" "string2"] "string3"])
 zip/next
 zip/next
 end?)


(->>
 (zip/vector-zip [1 ["string" "string2"] "string3"])
 zip/node
 end?)


(defn on [type]
  (fn [node state]
    (when-not (vector? node)
      {:next true})))

(defn all-strings []
  (fn [{args :args} _]
    (when-not (every? string? args)
      {:next true})))

(defmulti eval-expr symbol)
(defmethod eval-expr :default [x] x)
(defmethod eval-expr :concat [{args :args :as node}]
  (clojure.string/join args))
(defmethod eval-expr :compare-criteria [{:keys (left right) :as node}]
  (if (= left right) true node))

(defn node-eval [node state]
  {:node (eval-expr node)})

(defn chained-example [node]
  (:node
   (tree-visitor (tree-zipper node)
                 [(on :concat)
                  (all-strings)])))

(chained-example [["test" "test"] [1 2 "12 3"] ["4" "5" "6"]])

(->>
 (clojure.zip/vector-zip [["test" "test"] [1 2 "12 3"] ["4" "5" "6"]])
 next
 next)

(->>
 (tree-zipper ["test" "test" [1 2 "12 3" ["4" "5" "6"]]])
 zip/node
 next)

(next [1 2 3])
