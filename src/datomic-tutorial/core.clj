(ns datomic-tutorial.core
  (:require [datomic.client.api :as d]
            [clojure.edn :as edn]
            [clojure.pprint :as pp]))

(def client (-> "aws-config.edn"
                slurp
                edn/read-string
                d/client))

(def conn (d/connect client {:db-name "iteracode"}))

(def schema [{:db/ident       :list/title
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}

             {:db/ident       :list/todo
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/isComponent true}

             {:db/ident       :todo/description
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}

             {:db/ident       :todo/gid
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}])

(d/transact conn {:tx-data schema})

(def first-id (str (java.util.UUID/randomUUID)))
(def second-id (str (java.util.UUID/randomUUID)))
(d/transact conn {:tx-data
                  [{:list/title "learn datomic"
                    :list/todo [{:todo/gid first-id
                                 :todo/description "learn how to create a database"}
                                {:todo/gid second-id
                                 :todo/description "learn how to add the schema"}]}]})

(d/q '[:find ?gid ?description
       :where
       [?todo :todo/gid ?gid]
       [?todo :todo/description ?description]
       [?list :list/todo ?todo]
       [?list :list/title "learn datomic"]]
     (d/db conn))

;; **Pull query**
;; https://drewverlee.github.io/posts-output/2020-4-18-learn-datomic-part-2.html
;;
;; This type of query is available because we set :db/isComponent true in schema.
;;

(d/q '[:find (pull  ?list [*])
       :where
       [?list :list/title "learn datomic"]]
     (d/db conn))


;; **Updating entity**

(def db-id (ffirst (d/q '[:find ?todo
                          :in $ ?gid
                          :where
                          [?todo :todo/gid ?gid]]
                        (d/db conn)
                        first-id)))

(d/transact conn {:tx-data [{:db/id db-id :todo/description "hello, world"}]})

;; **Fetching history**
;; https://docs.datomic.com/cloud/tutorial/history.html#orgee6614f

(->> (d/q '[:find ?tx ?description ?op
            :in $ ?gid
            :where
            [?todo :todo/description ?description ?tx ?op]
            [?todo :todo/gid ?gid]]
          (d/history (d/db conn))
          first-id)
     (filter #(nth % 2)) ;; op at true means assertion, false retractions
     (sort-by first)
     (pp/pprint))
