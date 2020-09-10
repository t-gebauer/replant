(ns t-gebauer.replant.main
  (:require [t-gebauer.replant.parser :as parser]
            [t-gebauer.replant.generators.typescript :as typescript]
            [t-gebauer.replant.type-mapping :as type-mapping]
            ["fs" :as fs]))

(defn process [source]
  (let [parsed-class (parser/parse-class source)
        type-mapped-class (type-mapping/map-types parsed-class :kotlin :typescript)
        generated-content (typescript/generate-class type-mapped-class)]
    [type-mapped-class generated-content]))

(defonce cli-args (atom nil))

(defn main
  "Processes a single file. First argument: the file path"
  [& args]
  (reset! cli-args args)
  (prn args)
  (let [file-name (first args)
        file-content (.. fs (readFileSync file-name) toString)
        [parsed-class generated-ts] (process file-content)
        new-file-name (clojure.string/lower-case (str "out/" (:name parsed-class) ".ts"))]
    (println "Writing" file-name)
    (.. fs (writeFileSync new-file-name generated-ts))))

;; When build and started in non-production mode, the application will automatically
;; connect to the shadow-cljs server, reload changed classes on recompilation and
;; call this function.
(defn ^:dev/after-load start []
  (println "Clearing screen... \033[2J")
  (apply main @cli-args))
