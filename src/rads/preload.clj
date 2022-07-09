(ns rads.preload
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-namespaces []
  (let [file (io/file (str (System/getenv "HOME") "/.clojure/deps.edn"))]
    (when (.exists file)
      (-> (edn/read-string (slurp file))
          (get-in [:aliases :preload ::namespaces])))))

(defn -main [& main-args]
  (let [target-ns (symbol (nth main-args 1))
        wrapped-fn #(resolve (symbol (str target-ns) "-main"))
        wrapped-args (drop 2 main-args)
        namespaces (load-namespaces)]
    (doseq [preload-ns namespaces]
      (require preload-ns))
    (require target-ns)
    (apply (wrapped-fn) wrapped-args)))
