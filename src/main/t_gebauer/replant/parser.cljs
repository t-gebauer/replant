(ns t-gebauer.replant.parser
  (:require ["tree-sitter" :as Parser]
            ["tree-sitter-kotlin" :as language-kotlin]
            ["fs" :as fs]
            [cljs.test :refer (deftest is)]
            [cljs.reader :as reader])
  (:use [clojure.pprint :only [pprint]]))

(defn- print-string [string]
  (pprint (reader/read-string string)))
(defn- print-node [node]
  (print-string (.toString node)))

(def parser (new Parser))
(.setLanguage parser language-kotlin)

(defn- map-type [kotlin-type]
  (case kotlin-type
    ("Byte" "Short" "Int" "Long" "Float" "Double") "number"
    "String" "string"
    "Boolean" "boolean"
    "unknown"))
;; TODO Collections

(defn- extract-parameter [node]
  (let [children (.-children node)
        varVal (first (filter #(or (= (.-text %) "val") (= (.-text %) "var")) children))
        identifierNode (first (filter #(= (.-type %) "simple_identifier") children))
        typeNode (last children)        ; TODO there might be a default value after the type: `= "value"`
        isNullable (= "nullable_type" (.-type typeNode))
        type (if isNullable (.. typeNode -firstChild -text) (.. typeNode -text))]
    {:identifier (.-text identifierNode)
     :type (map-type type)
     :mutable (= "var" (.-text varVal))
     :nullable isNullable}))

(defn- extract-class [classNode]
  (let [className (.. classNode -children (find #(= (.-type %) "type_identifier")) -text)
        constructorNode (.. classNode -children (find #(= (.-type %) "primary_constructor")))
        parameterNodes (.. constructorNode -children (filter #(= (.-type %) "class_parameter")))
        parameters (map extract-parameter parameterNodes)]
    {:name className
     :parameters parameters}))

(defn parse-class [source]
  (let [tree (.. parser (parse source))
        classNode (.. tree -rootNode -children (find #(= (.-type %) "class_declaration")))
        class (extract-class classNode)]
    class))

(deftest should-parse-some-class-source
  (is (=
       {:name "Binary"
        :parameters [{:identifier "field1"
                      :type "number"
                      :mutable false
                      :nullable true}
                     {:identifier "BetterField"
                      :type "unknown"
                      :mutable true
                      :nullable false}]}
       (parse-class "data class Binary(val field1: Long?, var BetterField: LocalDate)"))))

(deftest should-parse-class-with-annotated-parameter
  (is (=
       {:name "Binary"
        :parameters [{:identifier "field1"
                      :type "number"
                      :mutable false
                      :nullable true}
                     {:identifier "BetterField"
                      :type "unknown"
                      :mutable true
                      :nullable false}]}
       (parse-class "import bar.foo.Something
                     import java.util.LocalDate
                     data class Binary(val field1: Long?, @Something var BetterField: LocalDate)"))))


(deftest should-parse-class-with-use-site-annotated-parameter
  (let [class (parse-class "class Binary(@field:Deps val branch: Boolean)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false}))))

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
  (let [class (parse-class "class Binary(@field:Deps() val branch: Boolean)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false}))))

;; (class_declaration
;;  (type_identifier)
;;  (ERROR (modifiers (annotation (simple_identifier))))
;;  (delegation_specifier
;;   (constructor_invocation
;;    (user_type (type_identifier))
;;    (value_arguments))))


(deftest should-parse-class-with-use-site-annotated-parameter-3
  (let [class (parse-class "class Binary(@field:Deps val branch: Boolean, var remedy: String)")]
    (is (= (:name class) "Binary") )
    (is (= (first (:parameters class))
           {:identifier "branch"
            :type "boolean"
            :mutable false
            :nullable false}))
    (is (= (second (:parameters class))
           {:identifier "remedy"
            :type "string"
            :mutable true
            :nullable false}))
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
