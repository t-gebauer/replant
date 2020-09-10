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

(defn- is-type-node? [node]
  (let [type (.-type node)]
    (or (= type "nullable_type") (= type "user_type"))))

(defn- extract-parameter [node]
  (let [children (.-children node)
        varVal (first (filter #(or (= (.-text %) "val") (= (.-text %) "var")) children))
        identifierNode (first (filter #(= (.-type %) "simple_identifier") children))
        typeNode (first (filter is-type-node? children))
        isNullable (= "nullable_type" (.-type typeNode))
        type (if isNullable (.. typeNode -firstChild -text) (.. typeNode -text))
        defaultValueNode (if-not (= (last children) typeNode) (last children))]
    {:identifier (.-text identifierNode)
     :type type
     :mutable (= "var" (.-text varVal))
     :nullable isNullable
     :default (if defaultValueNode (.-text defaultValueNode))}))

(defn- extract-class [classNode]
  (let [className (.. classNode -children (find #(= (.-type %) "type_identifier")) -text)
        constructorNode (.. classNode -children (find #(= (.-type %) "primary_constructor")))
        parameterNodes (.. constructorNode -children (filter #(= (.-type %) "class_parameter")))
        parameters (map extract-parameter parameterNodes)]
    {:name className
     :parameters parameters}))

(defn- preprocess
  "Hacky! We know that the parser currently can't handle use site annotated parameters.
  So we remove them upfront :)"
  [source]
  (clojure.string/replace source #"@\w+:" ""))

(defn parse-class [source]
  (let [source (preprocess source)
        tree (.. parser (parse source))
        classNode (.. tree -rootNode -children (find #(= (.-type %) "class_declaration")))
        class (extract-class classNode)]
    class))
