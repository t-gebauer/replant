(ns t-gebauer.replant.main-test
  (:require [t-gebauer.replant.main :as sut]
            [cljs.test :refer [deftest is] :as t :include-macros true]
            [t-gebauer.replant.test.diff :refer [diff]]))

(deftest full-test-class
  (let [source "
data class Apple(
    var id: Long?,
    val color: String,
    val size: Int,
    val updated: LocalDate
)
"
        [class output] (sut/process source)]
    (is (nil? (diff "export class Apple implements IApple {

  constructor(
    public id: number | null,
    public readonly color: string,
    public readonly size: number,
    public readonly updated: unknown,
  ) {}

  static from(obj: IApple): Apple {
    return new Apple(
      obj.id,
      obj.color,
      obj.size,
      obj.updated,
    )
  }

  copy(update: Partial<Apple>): Apple {
    return new Apple(
      update.id || this.id,
      update.color || this.color,
      update.size || this.size,
      update.updated || this.updated,
    )
  }
}

interface IApple {
  id: number | null
  readonly color: string
  readonly size: number
  readonly updated: unknown
}
" output)))))
