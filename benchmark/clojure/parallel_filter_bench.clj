(ns clojure.parallel-filter_bench
  (:require [clojure.test :refer :all])
  (:require [clojure.parallel-filter :refer :all])
  )

(deftest time-test-with-regular-pred
  (testing "testing filter vs pfilter with regular predicate"
    (let [l1 (list 1 2 3 4 5 6 7 8)
          l2 (list 3 7 1 5 8 4 2 6)]
      (println)
      (println "with filter:")
      (time (println (filter even? l1)))
      (time (println (filter even? l2)))
      (println)
      (println "with pfilter:")
      (time (println (parallel-filter even? l1)))
      (time (println (parallel-filter even? l2))))))

(deftest time-test-with-long-running-pred
  (testing "Naive execution time measure: "
    (let [l1 (list 1 2 3 4 5 6 7 8)
          l2 (list 3 7 1 5 8 4 2 6)]
      (println "load classes:")
      (time (println (parallel-filter long-running-pred (range 0 10))))
      (println)
      (println "with filter:")
      (time (println (filter long-running-pred l1)))
      (time (println (filter long-running-pred l2)))
      (time (println (filter long-running-pred (range 100))))
      (println)
      (println "with pfilter:")
      (time (println (parallel-filter long-running-pred l1)))
      (time (println (parallel-filter long-running-pred l2)))
      (time (println (parallel-filter long-running-pred (range 100))))))
  )