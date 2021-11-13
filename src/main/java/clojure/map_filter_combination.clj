(ns clojure.map-filter-combination
  (:require [clojure.map-filter :refer :all])
  (:require [clojure.string :as clojure-str]))

(defn add-letter [letter words]
  (map (fn [word] (str letter word))
       (filter (fn [word] (not (clojure-str/starts-with? word letter))) words)
       )
  )

(defn iter [letters words]
  (reduce concat
          []
          (map
            (fn [letter] (add-letter letter words))
            letters
            )
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
  (println (combinations 1 ["a" "b" "c"]))
  )