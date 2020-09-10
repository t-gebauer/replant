(ns t-gebauer.replant.generators.typescript-test
  (:require [t-gebauer.replant.generators.typescript :as sut]
            [cljs.test :refer [deftest is] :as t :include-macros true]))

(deftest should-generate-a-string-from-class-fragment
  (is (= "export class Apple {
  constructor(
    public readonly color: string | null,
    public size: number,
  ) {}
}
" (sut/generate-class {:name "Apple"
                             :parameters [{:identifier "color"
                                           :type "string"
                                           :mutable false
                                           :nullable true}
                                          {:identifier "size"
                                           :type "number"
                                           :mutable true
                                           :nullable false}]}))))
