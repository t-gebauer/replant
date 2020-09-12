(ns t-gebauer.replant.test.diff
  (:require ["diff-match-patch" :as diff-match-patch]
            ["colors/safe" :as colors]))

(def dpm (diff-match-patch.))

(defn- color [n]
  (case n
    -1 colors/red
    1 colors/green
    colors/white))

(defn diff
  "Uses googles diff-match-patch library to create a human readable diff and prints it to std.out
  Returns nil if there is no difference between the two strings or a representation of the diff."
  [expected actual]
  (let [diff (dpm.diff_main expected actual)
        _ (dpm.diff_cleanupSemantic diff)
        has-diff (or (> (count diff) 1)
                     (not (zero? (ffirst diff))))]
    (when has-diff
      (doseq [[n, d] diff]
        (print ((color n) d)))
      (js->clj diff))))
