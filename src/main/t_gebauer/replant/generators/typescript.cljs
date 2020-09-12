(ns t-gebauer.replant.generators.typescript
  (:require [cljs.test :refer (deftest is)]))

(defn- gen-param [{:keys [identifier type nullable mutable] :as param}]
  (str (if-not mutable "readonly ")
       identifier ": " type
       (if nullable " | null")))

(defn- gen-constructor [parameters]
  (str
   "  constructor(\n"
   (apply str (map #(str  "    public " (gen-param %) ",\n") parameters))
   "  ) {}\n"))

(defn- gen-static-from [{:keys [name parameters] :as class}]
  (let [identifiers (map :identifier parameters)]
    (str
     "  static from(obj: I" name "): " name " {\n"
     "    return new " name "(\n"
     (apply str (map #(str "      obj." % ",\n") identifiers))
     "    )"
     "\n  }\n")))

;; TODO "upd.x || this.x" does not work if upd.x is null on purpose
(defn- gen-copy-method [{:keys [name parameters] :as class}]
  (let [identifiers (map :identifier parameters)]
    (str
     "  copy(update: Partial<" name ">): " name " {\n"
     "    return new " name "(\n"
     (apply str (map #(str "      update." % " || this." % ",\n") identifiers))
     "    )"
     "\n  }\n")))

(defn- gen-interface [{:keys [name parameters] :as class}]
  (str
   "interface I" name " {\n"
   (apply str (map #(str  "  " (gen-param %) "\n") parameters))
   "}\n"))

(defn generate-class [{:keys [name parameters] :as class}]
  (str "export class " name " implements I" name " {\n"
       "\n"
       (gen-constructor parameters)
       "\n"
       (gen-static-from class)
       "\n"
       (gen-copy-method class)
       "}\n"
       "\n"
       (gen-interface class)))
