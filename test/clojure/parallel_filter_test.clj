(ns clojure.parallel-filter-test
  (:require [clojure.test :refer :all])
  (:require [clojure.parallel-filter :refer :all]))

(defn predicate? [x] (< x 5))

(deftest correctness-test
  (testing "testing pfilter correctness"
    (let [l1 (list 1 2 3 4 5 6 7 8)
          l2 (list 3 7 1 5 8 4 2 6)
          empty-l (list)]
      (is (= (filter predicate? l1) (parallel-filter predicate? l1)))
      (is (= (filter predicate? l2) (parallel-filter predicate? l2)))
      (is (= (filter predicate? empty-l) (parallel-filter even? empty-l)))
      (is (= (filter long-running-pred l1) (parallel-filter long-running-pred l1)))
      (is (= (filter long-running-pred l2) (parallel-filter long-running-pred l2)))
      (is (= (filter long-running-pred empty-l) (parallel-filter long-running-pred empty-l))))))