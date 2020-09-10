(ns t-gebauer.replant.type-mapping-test
  (:require [t-gebauer.replant.type-mapping :as sut]
            [cljs.test :as t :include-macros true]))


(t/deftest map-types
  (let [class {:parameters [{:type "Long"}
                            {:type "Boolean"}
                            {:type "LocalDate"}]}]
    (t/is (= (sut/map-types class :kotlin :typescript)
             {:parameters [{:type "number"}
                           {:type "boolean"}
                           {:type "unknown"}]}))))
