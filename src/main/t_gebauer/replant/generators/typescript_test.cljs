(ns t-gebauer.replant.generators.typescript-test
  (:require [t-gebauer.replant.generators.typescript :as sut]
            [t-gebauer.replant.test.diff :refer [diff]]
            [cljs.test :refer [deftest is] :as t :include-macros true]))

(def apple-class {:name "Apple"
                  :parameters [{:identifier "color"
                                :type "string"
                                :mutable false
                                :nullable true}
                               {:identifier "size"
                                :type "number"
                                :mutable true
                                :nullable false}]})

(deftest should-generate-class-constructor
  (is (nil? (diff "constructor(
    public readonly color: string | null,
    public size: number,
  ) {}" (re-find #"(?s)constructor\(.*?\) \{\}" (sut/generate-class apple-class))))))

(deftest should-generate-static-from-method
  (is (nil? (diff "static from(obj: IApple): Apple {
    return new Apple(
      obj.color,
      obj.size,
    )
  }" (re-find #"(?s)static from.*?\{.*?\}" (sut/generate-class apple-class))))))

(deftest should-generate-interface
  (is (nil? (diff
             "interface IApple {
  readonly color: string | null
  size: number
}" (re-find #"(?s)interface.*?\{.*?\}" (sut/generate-class apple-class))))))

(deftest should-include-copy-method
  (is (nil? (diff
             "copy(update: Partial<Apple>): Apple {
    return new Apple(
      update.color || this.color,
      update.size || this.size,
    )
  }" (re-find #"(?s)copy.*?\{.*?\}" (sut/generate-class apple-class))))))
