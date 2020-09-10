(ns t-gebauer.replant.main-test
  (:require [t-gebauer.replant.main :as sut]
            [cljs.test :refer [deftest is] :as t :include-macros true]))

(deftest basic-data-class
  (let [source "data class User(val name: String, val age: Int)"
        [class output] (sut/process source)]
    (is (= "export class User {
  constructor(
    public readonly name: string,
    public readonly age: number,
  ) {}
}
" output))))

(deftest basic-data-class-with-default-parameters
  (let [source "data class User(val name: String = \"\", val age: Int = 0)"
        [class output] (sut/process source)]
    (is (= "export class User {
  constructor(
    public readonly name: string,
    public readonly age: number,
  ) {}
}
" output))))
