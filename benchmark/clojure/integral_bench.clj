(ns clojure.integral-bench
  (:require [clojure.test :refer :all])
  (:require [clojure.integral :refer :all])
  )

(deftest time-test
  (testing "Naive execution time measure: "
    (println "y=sqrx integral: ")
    (time (integral y=sqrx 1))
    (time (integral y=sqrx 1))
    (time (integral y=sqrx 2))
    (time (integral y=sqrx 2))
    (time (integral y=sqrx 30))
    (time (integral y=sqrx 30))
    (println)
    (println "y=sqrx memoized: ")
    (time ((memoized-integral y=sqrx) 1))
    (time ((memoized-integral y=sqrx) 1))
    (time ((memoized-integral y=sqrx) 2))
    (time ((memoized-integral y=sqrx) 2))
    (time ((memoized-integral y=sqrx) 30))
    (time ((memoized-integral y=sqrx) 30)))
  )