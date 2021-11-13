(ns clojure.integral-bench1
  (:require [clojure.test :refer :all])
  (:require [clojure.integral :refer :all])
  )

(deftest time-test
  (testing "Naive execution time measure: "
    (println "y=sqrx with integral:")
    (time ((memoized-integral y=sqrx) 1))
    (time ((memoized-integral y=sqrx) 1))
    (time ((memoized-integral y=sqrx) 2))
    (time ((memoized-integral y=sqrx) 2))
    (time ((memoized-integral y=sqrx) 30))
    (time ((memoized-integral y=sqrx) 30))
    (println)
    (println "y=sqrx with partial sum integral:")
    (time ((integral-partial-sums y=sqrx) 1))
    (time ((integral-partial-sums y=sqrx) 1))
    (time ((integral-partial-sums y=sqrx) 2))
    (time ((integral-partial-sums y=sqrx) 2))
    (time ((integral-partial-sums y=sqrx) 30))
    (time ((integral-partial-sums y=sqrx) 30)))
  )
