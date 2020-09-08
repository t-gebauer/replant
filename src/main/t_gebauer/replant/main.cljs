(ns t-gebauer.replant.main
  (:require [t-gebauer.replant.parser :as parser]
            [t-gebauer.replant.generators.typescript :as typescript]
            ["fs" :as fs]))

(defn process
  "Input: a source string representing Kotlin code
   @returns a string representing an equivalent TypeScript class"
  [source]
  (-> source
      parser/parse-class
      typescript/generate-class))

(defonce cli-args (atom nil))

(defn main
  "Processes a single file. First argument: the file path"
  [& args]
  (reset! cli-args args)
  (prn args)
  (let [file-name (first args)
        file-content (.. fs (readFileSync file-name) toString)
        parsed-class (parser/parse-class file-content)
        generated-ts (typescript/generate-class parsed-class)]
    (println "Writing" file-name)
    (.. fs (writeFileSync (clojure.string/lower-case (str "out/" (:name parsed-class) ".ts")) generated-ts))))

;; When build and started in non-production mode, the application will automatically
;; connect to the shadow-cljs server, reload changed classes on recompilation and
;; call this function.
(defn ^:dev/after-load start []
  (println "Clearing screen... \033[2J")
  (apply main @cli-args))
