(ns clojure.parallel_filter_test1
  (:require [clojure.test :refer :all])
  (:require [clojure.parallel-filter :refer :all])
  )

(def naturals (iterate inc 1))

(deftest correctness-test-lazy
  (testing "testing pfilter-lazy correctness"
    (let [l1 (list 1 2 3 4 5 6 7 8)
          l2 (list 3 7 1 5 8 4 2 6)
          empty-l (list)]
      (is (= (filter even? l1) (parallel-filter-lazy even? l1)))
      (is (= (filter even? l2) (parallel-filter-lazy even? l2)))
      (is (= (filter even? empty-l) (parallel-filter-lazy even? empty-l)))
      (is (= (filter long-running-pred l1) (parallel-filter-lazy long-running-pred l1)))
      (is (= (filter long-running-pred l2) (parallel-filter-lazy long-running-pred l2)))
      (is (= (filter long-running-pred empty-l) (parallel-filter-lazy long-running-pred empty-l)))
      (is (= (take 10 (filter even? naturals)) (take 10 (parallel-filter-lazy even? naturals))))))
  )