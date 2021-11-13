(ns clojure.dnf
  (:require [clojure.test :refer :all])
  (:require [clojure.dnf.dnf :refer :all]))

(deftest test-rules
  (testing "testing rules"
    (println "a -> b == !a || !b"
             (is (= (disjunction (negation (variable :a)) (variable :b))
                    (to-dnf (implication (variable :a) (variable :b))))))
    (println "!(a && b && c) == (!a || !b || !c) "
             (is (= (disjunction (negation (variable :a)) (negation (variable :b)) (negation (variable :c)))
                    (to-dnf (negation (conjunction (variable :a) (variable :b) (variable :c)))))))
    (println "!(a || b || c) == (!a && !b && !c) "
             (is (= (conjunction (negation (variable :a)) (negation (variable :b)) (negation (variable :c)))
                    (to-dnf (negation (disjunction (variable :a) (variable :b) (variable :c)))))))
    (println "!!a == a "
             (is (= (variable :a)
                    (to-dnf (negation (negation (variable :a)))))))
    (println "(a && b && (c || d)) == ((c && a && b) || (d && a && b)) "
             (is (= (disjunction (conjunction (variable :c) (variable :a) (variable :b)) (conjunction (variable :d) (variable :a) (variable :b)))
                    (to-dnf (conjunction (variable :a) (variable :b) (disjunction (variable :c) (variable :d)))))))
    (println "(a || b || a) == (a || b) "
             (is (= (disjunction (variable :a) (variable :b))
                    (to-dnf (disjunction (variable :a) (variable :b) (variable :a))))))
    (println "(a && b && a) == (a && b) "
             (is (= (conjunction (variable :a) (variable :b))
                    (to-dnf (conjunction (variable :a) (variable :b) (variable :a))))))
    (println "!(a -> b) == (a && !b) "
             (is (= (conjunction (variable :a) (negation (variable :b)))
                    (to-dnf (negation (implication (variable :a) (variable :b)))))))
    (println "(0 && a) == 0 "
             (is (= (constant false)
                    (to-dnf (conjunction (constant false) (variable :a))))))
    (println "(1 || a) == 1 "
             (is (= (constant true)
                    (to-dnf (disjunction (constant true) (variable :a))))))
    (println "!0 = 1  "
             (is (= (constant true)
                    (to-dnf (negation (constant false))))))
    (println "(0 -> a) == 1 "
             (is (= (constant true)
                    (to-dnf(implication (constant false) (variable :a))))))
    (println "!(1 || a) == 0 "
             (is (= (constant false)
                    (to-dnf (negation (disjunction (constant true) (variable :a)))))))
    (println "or (a) = a "
             (is (= (variable :a)
                    (to-dnf (disjunction (variable :a))))))
    (println "and (a) = a"
             (is (= (variable :a)
                    (to-dnf (conjunction (variable :a))))))
    (println "!((a && (c || b)) && (a && a)) == false, [a=true, b=false, c=true] "
             (is (false? (substitute-expr (negation (conjunction (conjunction (variable :a) (disjunction (variable :c) (variable :b)))
                                                                 (conjunction (variable :a) (variable :a)))) {:a true :b false :c true}))))
    (println "((a && !a) || (a ||!a)) == true, [a=true, b=false] "
             (is (true? (substitute-expr (disjunction (conjunction (variable :a) (negation (variable :a))) (disjunction (variable :a) (negation (variable :a)))) {:a true :b false}))))
    (println "((&& 1) || a) == true, [a=true] "
             (is (true? (substitute-expr (disjunction (conjunction (constant true) ) (variable :a)) {:a true}))))
    (println "((!0 && a) || !(a || 0)) == true, [a=true] "
             (is (true? (substitute-expr(disjunction (conjunction (negation (constant false)) (variable :a)) (negation (disjunction (variable :a) (constant false)))) {:a true}))))
    (println "((1 && a) && a) == true, [a=true] "
             (is (true? (substitute-expr (conjunction (conjunction (constant true) (variable :a)) (variable :a)) {:a true}))))
    (println "((0 && a) && a) == false, [a=true] "
             (is (false? (substitute-expr (conjunction (conjunction (constant false) (variable :a)) (variable :a)) {:a true}))))
    (println "((1 || a) || a) == true, [a=false] "
             (is (true? (substitute-expr (disjunction (disjunction (constant true) (variable :a)) (variable :a)) {:a false}))))
    ))
