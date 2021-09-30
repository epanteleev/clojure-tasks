(ns combination
  (:require [clojure.string :as clojure-str]))

(defn add-letter
  ([symbol, words] (add-letter (first words) (rest words) symbol []))
  ([x, tail, symbol, acc]
   (if-not (nil? x)
     (if-not (clojure-str/starts-with? x symbol)
       (tail (first tail) (rest tail) symbol (conj acc (str symbol x)))
       (tail (first tail) (rest tail) symbol acc)
       )
     acc
     )
   )
  )

(defn iter
  ([symbols, words] (iter (first symbols) (rest symbols) words []))
  ([x, tail, words, acc]
   (if-not (nil? x)
     (recur (first tail) (rest tail) words (concat acc (add-letter x words)))
     acc)
   )
  )

(defn combinations
  ([n, symbols] (combinations n symbols symbols))
  ([n, symbols, words]
   (if
     (> n 1)
     (combinations (- n 1) symbols (iter symbols words))
     words)
   )
  )

(defn ^:private -main []
  (println (combinations 2 ["a" "b" "c"]))
  )
