(ns clojure.map-filter)

(defn my-map
  [f, coll]
  (reduce
    (fn [acc elem]
      (concat acc (list (f elem))))
    []
    coll
    )
  )

(defn my-filter
  [f coll]
  (reduce
    (fn [acc elem]
      (if
        (f elem)
        (concat acc (list elem))
        acc
        )
      )
    []
    coll
    )
  )

(defn ^:private -main []
  (println (my-map (fn [x] (+ x 1)) [1]))
  (println (my-map (fn [x] (+ x 1)) [1 2 3 4]))
  (println (my-filter (fn [x] (> x 2)) [1 2 3 4 5]))
  )


