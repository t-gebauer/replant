(ns t-gebauer.replant.generators.typescript
  (:require [cljs.test :refer (deftest is)]))

(defn- create-param [param]
  (str "  " (:identifier param) ": " (:type param) (if (:nullable param) " | null")))

(defn generate-class [class]
  (str "export class " (:name class) " {\n"
       (apply str (interpose "\n" (map create-param (:parameters class))))
       "\n}\n"))

(deftest should-generate-a-string-from-class-fragment
  (is (=
"export class Apple {
  color: string | null
}
"
       (generate-class {:name "Apple"
                        :parameters [{:identifier "color"
                                      :type "string"
                                      :mutable false
                                      :nullable true}]}))))
