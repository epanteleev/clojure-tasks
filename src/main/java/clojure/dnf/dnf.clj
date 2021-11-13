(ns clojure.dnf.dnf
  (:require [clojure.core.match])
  )

;; Primitives

(defn variable [name]
  "Represent the variable of dnf."
  {:pre [(keyword? name)]}
  (list ::var name)
  )

(defn variable? [expr]
  "Return true if head of expr is variable."
  (= (first expr) ::var)
  )

(defn constant [value]
  "Represent constant of dnf."
  (list ::const value)
  )

(defn constant? [expr]
  "Return true if head of expr is constant."
  (= (first expr) ::const)
  )

(defn ^:private constant-value [expr]
  (second expr)
  )

;; Operations

(defn conjunction [expr & rest]
  (cons ::and (cons expr rest))
  )

(defn conjunction? [expr]
  (= (first expr) ::and)
  )

(defn disjunction [expr & rest]
  (cons ::or (cons expr rest))
  )

(defn disjunction? [expr]
  (= (first expr) ::or)
  )

(defn negation [expr]
  (list ::not expr)
  )

(defn negation? [expr]
  (= (first expr) ::not)
  )

(defn implication [expr1 expr2]
  (list ::impl expr1 expr2)
  )

(defn implication? [expr]
  (= (first expr) ::impl)
  )

;; Util functions-

(defn ^:private args [expr]
  (rest expr)
  )

(def ^:private dnf-rules
  "DNF rules"
  (list
    ; (a -> b) => (!a || b)
    [implication?
     (fn [expr] (disjunction (negation (first (args expr))) (second (args expr))))]

    ; !(a && b && ...) => (!a || !b || ...)
    [(fn [expr] (and (negation? expr) (conjunction? (first (args expr)))))
     (fn [expr]
       (apply disjunction
              (map negation (args (first (args expr))))))]

    ; !(a || b || ...) => (!a && !b && ...)
    [(fn [expr]
       (and (negation? expr) (disjunction? (first (args expr)))))
     (fn [expr]
       (apply conjunction
              (map negation (args (first (args expr))))))]

    ; (!!a) => (a)
    [(fn [expr]
       (and (negation? expr) (negation? (first (args expr)))))
     (fn [expr]
       (first (args (first (args expr)))))]

    ; Conj or disj with 1 arg
    [(fn [expr]
       (and (or (disjunction? expr) (conjunction? expr)) (= (count (args expr)) 1)))
     (fn [expr]
       (first (args expr)))]

    ; Conj with false is false
    [(fn [expr] (and (conjunction? expr) (some constant? (args expr))))
     (fn [expr]
       (let [const-arg (filter constant? (args expr))
             false-const (some (fn [arg] (if (= (constant-value arg) false) arg false)) const-arg)]
         (if (empty? false-const) (cons ::and (args expr)) (constant false))))]

    ; Disj with true is true
    [(fn [expr] (and (disjunction? expr) (some constant? (args expr))))
     (fn [expr]
       (let [const-arg (filter constant? (args expr))
             true-const (some (fn [arg] (if (= (constant-value arg) true) arg false)) const-arg)]
         (if (empty? true-const) (cons ::or (args expr)) (constant true))))]

    ; (a && b ... && (c || d || ...) && ...) => ((a && b && ... && c ) || (a & b & ... & d) || ...)
    [(fn [expr]
       (and (conjunction? expr) (some disjunction? (args expr))))
     (fn [expr]
       (let [disj-arg (some (fn [arg] (if (disjunction? arg) arg false)) (args expr))
             rest-args (remove (fn [expr] (= expr disj-arg)) (args expr))]
         (apply disjunction (map (fn [arg] (apply conjunction (cons arg rest-args))) (args disj-arg)))))]

    ; (a && a && ...) => (a && ...) ,    (a || a || ...) => (a || ...)
    [(fn [expr]
       (and (or (disjunction? expr) (conjunction? expr)) (not (apply distinct? (args expr)))))
     (fn [expr]
       (if (disjunction? expr)
         (cons ::or (distinct (args expr)))
         (cons ::and (distinct (args expr)))
         )
       )]

    ; !1 = 0, !0 = 1
    [(fn [expr] (and (negation? expr) (constant? (first (args expr)))))
     (fn [expr] (if (= false (constant-value (first (args expr))))
                  (constant true)
                  (constant false)))]
    )
  )

(defn ^:private find-rule [rules expr]
  (some
    (fn [rule] (if ((first rule) expr) (second rule) false))
    rules)
  )

(defn ^:private apply-dnf-rules [expr]
  (if (or (constant? expr) (variable? expr))
    expr
    (let [expr2 (cons (first expr) (map apply-dnf-rules (args expr)))
          rule (find-rule dnf-rules expr2)]
      (if rule (rule expr2) expr2)
      )
    )
  )

(defn ^:private to-dnf-iter [expr previous]
  (if (= expr previous)
    expr
    (recur (apply-dnf-rules expr) expr))
  )

(defn to-dnf [expr]
  "Convert given expression to dnf"
  (to-dnf-iter (apply-dnf-rules expr) expr)
  )

(defn ^:private get-variable-value [values name]
  (if (contains? values name)
    (get values name)
    (throw (Exception. (str "Unknown variable: " name)))
    )
  )

(defn ^:private substitute-expr-iter
  [values expr]
  (cond
    (constant? expr) (first (args expr))
    (variable? expr) (get-variable-value values (first (args expr)))
    (negation? expr) (not (substitute-expr-iter values (first (args expr))))
    (conjunction? expr) (reduce (fn [acc val] (and acc (substitute-expr-iter values val))) true (args expr))
    (disjunction? expr) (reduce (fn [acc val] (or acc (substitute-expr-iter values val))) false (args expr)))
  )

(defn substitute-expr [expr values]
  "Eval given expression and substitute values"
  (substitute-expr-iter values (to-dnf expr))
  )