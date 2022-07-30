(ns rads.preload
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-namespaces []
  (let [file (io/file (str (System/getenv "HOME") "/.clojure/deps.edn"))]
    (when (.exists file)
      (-> (edn/read-string (slurp file))
          (get-in [:aliases :preload ::namespaces])))))

(defn warn [& messages]
  (apply println "WARNING: rads.preload:" messages))

(defn handle-error [error preload-ns]
  (warn "Failed to require namespace:" preload-ns)
  (warn "Are you calling clj/clojure with the :preload alias?")
  (warn "Error output:")
  (println)
  (println error)
  (println))

(defn -main [& main-args]
  (let [split-args (split-with #(not= % "-m") main-args)
        [_ [_ target-ns & target-args]] split-args
        namespaces (load-namespaces)]
    (doseq [preload-ns namespaces]
      (try
        (require preload-ns)
        (catch Throwable e
          (handle-error e preload-ns))))
    (apply (requiring-resolve (symbol (str target-ns) "-main"))
           target-args)))
