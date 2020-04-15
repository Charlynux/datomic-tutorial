(ns datomic-tutorial.core
  (:require [datomic.client.api :as d]
            [clojure.edn :as edn]))

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
              :db/cardinality :db.cardinality/one}])

(d/transact conn {:tx-data schema})

(d/transact conn {:tx-data [{:list/title "learn datomic"
                             :list/todo [{:todo/description "learn how to create a database"}
                                         {:todo/description "learn how to add the schema"}]}]})

(d/q '[:find ?description
       :where
       [?todo :todo/description ?description]
       [?list :list/todo ?todo]
       [?list :list/title "learn datomic"]]
     (d/db conn))
