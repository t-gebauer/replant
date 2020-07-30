(ns t-gebauer.replant)

(defonce cli-args (atom nil))

(defn main [& args]
  (reset! cli-args args)
  (println "hello worlds")
  (prn args))

(defn ^:dev/after-load start []
  (println "Restarting")
  (apply main @cli-args))
