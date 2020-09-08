(ns t-gebauer.replant.main-test
  (:require [t-gebauer.replant.main :as sut]
            [cljs.test :refer [deftest is] :as t :include-macros true]))

(deftest basic-data-class
  (let [source "data class User(val name: String, val age: Int)"]
    (is (=
"export class User {
  readonly name: string
  readonly age: number
}
" (sut/process source)))))

(deftest basic-data-class-with-default-parameters
  (let [source "data class User(val name: String = \"\", val age: Int = 0)"]
    (is (=
"export class User {
  readonly name: string
  readonly age: number
}
" (sut/process source)))))
