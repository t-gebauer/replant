(ns t-gebauer.replant.parser
  (:require ["tree-sitter" :as Parser]
            ["tree-sitter-kotlin" :as language-kotlin]
            ["fs" :as fs]
            [cljs.test :refer (deftest is)])
  (:use [clojure.pprint :only [pprint]]))

(def parser (new Parser))
(.setLanguage parser language-kotlin)

(defn- map-type [kotlin-type]
  (case kotlin-type
    ("Byte" "Short" "Int" "Long" "Float" "Double") "number"
    "String" "string"
    "unknown"))

(defn- extract-parameter [node]
  (let [children (.-children node)
        varVal (first (filter #(or (= (.-text %) "val") (= (.-text %) "var")) children))
        identifierNode (first (filter #(= (.-type %) "simple_identifier") children))
        typeNode (last children)
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
       (parse-class "import bar.foo.Something\nimport java.util.LocalDate\n data class Binary( val field1: Long?, @Something var BetterField: LocalDate)"))))


;; TODO fixme
;; (deftest should-parse-class-with-field-annotated-parameter
;;   (is (=
;;        {:name "Binary"
;;         :parameters [{:identifier "field1"
;;                       :type "number"
;;                       :mutable false
;;                       :nullable true}
;;                      {:identifier "BetterField"
;;                       :type "unknown"
;;                       :mutable true
;;                       :nullable false}]}
;;        (parse-class "import bar.foo.Something\nimport java.util.LocalDate\n data class Binary( val field1: Long?, @field:Something var BetterField: LocalDate)"))))
