(ns tw2.c.log)

(defn dbg [obj]
  (#?(:clj clojure.pprint/pprint
      :cljs cljs.pprint/pprint)
   obj)
  obj)

(defn dbg-str [obj]
  (with-out-str (dbg obj)))
