(ns t-gebauer.replant.generators.typescript
  (:require [cljs.test :refer (deftest is)]))

(defn- create-param [{:keys [identifier type nullable mutable] :as param}]
  (str "    public "
       (if-not mutable "readonly ")
       identifier ": " type
       (if nullable " | null")
       ","))

(defn generate-class [{:keys [name parameters] :as class}]
  (str "export class " name " {\n"
       "  constructor(\n"
       (apply str (interpose "\n" (map create-param parameters)))
       "\n  ) {}"
       "\n}\n"))

