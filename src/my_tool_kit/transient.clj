(ns my-tool-kit.transient)

(defn vrange [n]
  (loop [i 0 v []]
    (if (< i n)
      (recur (inc i) (conj v i))
      v)))

(nil? [1])
(empty? {})
(empty? [])

(import java.io.File)
(file-seq (new File "~/Documents/elisp"))
