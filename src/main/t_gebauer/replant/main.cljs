(ns t-gebauer.replant.main
  (:require [t-gebauer.replant.parser :as parser]
            [t-gebauer.replant.generators.typescript :as typescript]
            ["fs" :as fs]))

(defonce cli-args (atom nil))

(defn main [& args]
  (reset! cli-args args)
  (prn args)
  (let [file-name (first args)
        file-content (.. fs (readFileSync file-name) toString)
        parsed-class (parser/parse-class file-content)
        generated-ts (typescript/generate-class parsed-class)]
    (println "Writing" file-name)
    (.. fs (writeFileSync (clojure.string/lower-case (str "out/" (:name parsed-class) ".ts")) generated-ts))))

(defn ^:dev/after-load start []
  (println "Clearing screen... \033[2J")
  (apply main @cli-args))
