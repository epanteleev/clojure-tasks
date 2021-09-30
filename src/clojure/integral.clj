(ns clojure.integral)

(def step 0.00005)

(def eps 0.001)

(defn abs [x]
  (max x (- x))
  )

(defn equal-to [a b]
  (< (abs (- a b)) eps)
  )

(defn y=const [_] 1)

(defn y=x [x] x)

(defn y=sqrx [x] (* x x))

(defn ^:private trapezoidal-rule [fa fb]
  (/ (* (+ fa fb) step) 2.0))

(defn ^:private trapezoidal-rule-iter [f i]
  (trapezoidal-rule (f (* i step)) (f (* (dec i) step))))

(defn ^:private size-of-steps [interval] (int (/ interval step)))

(defn ^:private integral-iter
  ([f n] (integral-iter f n 0.0))
  ([f n acc]
   (
     if (> n 0)
     (let [partial-sum (trapezoidal-rule-iter f n)]
       (recur f (dec n) (+ acc partial-sum)))
     acc
     )
   )
  )

(defn memoized-integral [f]
  (fn [x] (let [memoized-integral-iter (memoize integral-iter)]
            (memoized-integral-iter f (size-of-steps x))))
  )

(defn integral [f x]
  (integral-iter f (size-of-steps x))
  )

(defn ^:private lazy-partial-integral-sums [f]
  (map first (iterate
               (fn [[sum i]] [(+ sum (trapezoidal-rule-iter f (inc i))) (inc i)])
               [0 0])
       )
  )

(defn integral-partial-sums [f]
  (fn [x] (let [n (size-of-steps x)]
            (let [memoized-lazy-partial-integral-sums (memoize lazy-partial-integral-sums)]
              (nth (memoized-lazy-partial-integral-sums f) n)
              )
            ))
  )