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
  (let [firstChildNode (first (.-children node))
        children (.-children node)
        typeNode (nth children 3)
        isNullable (= "nullable_type" (.-type typeNode))
        type (if isNullable (.. typeNode -firstChild -text) (.. typeNode -text))]
    {:identifier (.-text (second children))
     :type (map-type type)
     :mutable (= "var" (.-text (first children)))
     :nullable isNullable}))

(defn- extract-class [classNode]
  (let [className (.. classNode -children (find #(= (.-type %) "type_identifier")) -text)
        constructorNode (.. classNode -children (find #(= (.-type %) "primary_constructor")))
        parameterNodes (.. constructorNode -children (filter #(= (.-type %) "class_parameter")))
        parameters (map extract-parameter parameterNodes)]
    {:name className
     :parameters parameters}))

(defn- create-ts-param [param]
  (str "  " (:identifier param) ": " (:type param) (if (:nullable param) " | null")))

(defn- create-ts-class [class]
  (str "export class " (:name class) " {\n"
       (apply str (interpose "\n" (map create-ts-param (:parameters class))))
       "\n}\n"))

(defn parse-file [file-name]
  (println "Parsing" file-name)
  (let [source (.. fs (readFileSync file-name) toString)
        tree (.. parser (parse source))
        classNode (.. tree -rootNode -children (find #(= (.-type %) "class_declaration")))
        class (extract-class classNode)
        result (create-ts-class class)]
    (println source)
    (pprint class)
    (println result)
    (println "Writing" file-name)
    (.. fs (writeFileSync (clojure.string/lower-case (str "out/" (:name class) ".ts")) result))))

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
       (parse-class "data class Binary( val field1: Long?, @Something var BetterField: LocalDate)"))))

