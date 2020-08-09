(ns t-gebauer.replant.main
  (:require [t-gebauer.replant.parser :as parser]))

(defonce cli-args (atom nil))

(defn main [& args]
  (reset! cli-args args)
  (prn args)
  (let [file-name (first args)]
    (parser/parse-file file-name)))

(defn ^:dev/after-load start []
  (println "Clearing screen... \033[2J")
  (apply main @cli-args))
