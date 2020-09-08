(ns t-gebauer.replant.parser-test
  (:require [t-gebauer.replant.parser :as sut]
            [cljs.test :refer [deftest is] :as t :include-macros true]))


(deftest should-parse-some-class-source
  (is (=
       {:name "Binary"
        :parameters [{:identifier "field1"
                      :type "number"
                      :mutable false
                      :nullable true
                      :default nil}
                     {:identifier "BetterField"
                      :type "unknown" ; TODO Add some way to map to a custom type
                      :mutable true
                      :nullable false
                      :default nil}]}
       (sut/parse-class "data class Binary(val field1: Long?, var BetterField: LocalDate)"))))

(deftest should-parse-class-with-annotated-parameter
  (is (=
       {:name "Binary"
        :parameters [{:identifier "field1"
                      :type "number"
                      :mutable false
                      :nullable true
                      :default nil}
                     {:identifier "BetterField"
                      :type "unknown"
                      :mutable true
                      :nullable false
                      :default nil}]}
       (sut/parse-class "import bar.foo.Something
                     import java.util.LocalDate
                     data class Binary(val field1: Long?, @Something var BetterField: LocalDate)"))))


(deftest should-parse-class-with-use-site-annotated-parameter
  (let [class (sut/parse-class "class Binary(@field:Deps val branch: Boolean)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false
            :default nil}))))

;; (class_declaration
;;  (type_identifier)
;;  (primary_constructor
;;   (ERROR
;;    (class_parameter
;;     (modifiers (annotation (simple_identifier)))
;;     (simple_identifier (MISSING "_lexical_identifier_token1"))
;;     (user_type (type_identifier))))
;;   (class_parameter (simple_identifier) (user_type (type_identifier)))))


(deftest should-parse-class-with-use-site-annotated-parameter-2
  (let [class (sut/parse-class "class Binary(@field:Deps() val branch: Boolean)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false
            :default nil}))))

;; (class_declaration
;;  (type_identifier)
;;  (ERROR (modifiers (annotation (simple_identifier))))
;;  (delegation_specifier
;;   (constructor_invocation
;;    (user_type (type_identifier))
;;    (value_arguments))))


(deftest should-parse-class-with-use-site-annotated-parameter-3
  (let [class (sut/parse-class "class Binary(@field:Deps val branch: Boolean, var twig: String)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false
            :default nil}))
    (is (= (second (:parameters class))
           {:identifier "twig"
            :type "string"
            :mutable true
            :nullable false
            :default nil}))
    (is (= (count (:parameters class)) 2))))

;; (class_declaration
;;  (type_identifier)
;;  (primary_constructor
;;   (ERROR
;;    (class_parameter
;;     (modifiers (annotation (simple_identifier)))
;;     (simple_identifier (MISSING "_lexical_identifier_token1"))
;;     (user_type (type_identifier))))
;;   (class_parameter (simple_identifier) (user_type (type_identifier)))
;;   (class_parameter (simple_identifier) (user_type (type_identifier)))))

(deftest should-handle-default-parameters
  (let [class (sut/parse-class "data class User(val name: String = \"\", val age: Int = 0)")]
    (is (= (:name class) "User") )
    (is (= (first (:parameters class))
           {:identifier "name"
            :type "string"
            :mutable false
            :nullable false
            :default "\"\""}))
    (is (= (second (:parameters class))
           {:identifier "age"
            :type "number"
            :mutable false
            :nullable false
            :default "0"}))
    (is (= (count (:parameters class)) 2))))
