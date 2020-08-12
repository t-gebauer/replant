(ns t-gebauer.replant.main
  (:require [t-gebauer.replant.parser :as parser]
            ["fs" :as fs]))

(defonce cli-args (atom nil))

(defn main [& args]
  (reset! cli-args args)
  (prn args)
  (let [file-name (first args)
        file-content (.. fs (readFileSync file-name) toString)
        parsed-class (parser/parse-class file-content)]
    (parser/parse-file file-name)))

(defn ^:dev/after-load start []
  (println "Clearing screen... \033[2J")
  (apply main @cli-args))
