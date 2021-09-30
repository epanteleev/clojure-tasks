(ns clojure.parallel-filter)

(def ^:private tack-pool 10)

(defn long-running-pred [x]
  (reduce (fn [acc x] (+ acc x)) 0 (take 1000000 (range)))
  (even? x)
  )

(defn ^:private split-to
  ([n coll]
   (let [c (count coll) batch-size (Math/ceil (/ c n))]
     (split-to batch-size coll n n (list)))
   )
  ([batch-size coll current-step steps acc]
   (if (= current-step 0)
     (reverse acc)
     (let [tack (drop batch-size coll) next-step (dec current-step)]
       (recur batch-size tack next-step steps (cons (take batch-size coll) acc))
       )
     )
   )
  )

(defn parallel-filter
  [pred coll] (->> (split-to tack-pool coll)
                   (pmap (fn [x] (doall (filter pred x))))
                   (flatten))
  )

(defn ^:private lazy-split [batch-size coll]
  (when (not (empty? coll))
    (cons
      (take batch-size coll)
      (lazy-seq (lazy-split batch-size (drop batch-size coll)))
      )
    )
  )

(defn parallel-filter-lazy
  ([pred coll] (parallel-filter-lazy pred coll 3))
  ([pred coll batches]
   (->> (lazy-split batches coll)
        (lazy-split tack-pool)
        (mapcat (fn [block]
                  (->> block
                       (pmap #(doall (filter pred %)))
                       ))
                )
        (flatten)
        )
   )
  )