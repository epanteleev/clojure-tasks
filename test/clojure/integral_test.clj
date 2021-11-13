(ns clojure.integral-test
  (:require [clojure.test :refer :all])
  (:require [clojure.integral :refer :all])
  )

(deftest y=const-test
  (testing "testing integral y=const"
    (is (equal-to 1.0 (integral y=const 1)))
    (is (equal-to 2.0 ((memoized-integral y=const) 2))))
  )

(deftest y=x-test
  (testing "testing integral y=x"
    (is (equal-to 0.5 ((memoized-integral y=x) 1)))
    (is (equal-to 2.0 ((memoized-integral y=x) 2)))
    (is (equal-to 96.605 ((memoized-integral y=x) 13.9))))
  )

(deftest y=sqrx-test
  (testing "testing integral y=sqrx"
    (is (equal-to 0.333 ((memoized-integral y=sqrx) 1)))
    (is (equal-to 576 ((memoized-integral y=sqrx) 12))))
  )