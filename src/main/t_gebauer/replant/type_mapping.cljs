(ns t-gebauer.replant.type-mapping)

(def type-maps (volatile! {}))

(defn register-type-map [source target type-map]
  (vswap! type-maps assoc [source target] type-map))

(defn get-type-map [source target]
  (get @type-maps [source target]))

(defn map-types [{:keys [parameters] :as class} source target]
  (let [type-map (get-type-map source target)]
    (assoc class :parameters
           (map (fn [param]
                  (update param :type
                          #(or (type-map %) (type-map :unknown))))
                parameters))))

(register-type-map
 :kotlin :typescript
 {"Int" "number"
  "Long" "number"
  "Float" "number"
  "Double" "number"
  "Boolean" "boolean"
  "String" "string"
  :unknown "unknown"})
