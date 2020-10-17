(ns datomic-tutorial.core
  (:require [datomic.client.api :as d]
            [clojure.edn :as edn]
            [clojure.pprint :as pp]))

(def client (d/client {:server-type :dev-local :system "dev"}))

(def db-config {:db-name "todos"})

(comment (d/delete-database client db-config))

(d/create-database client db-config)

(def conn (d/connect client db-config))

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
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity}])

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

(d/q '[:find (pull ?list [*])
       :where
       [?list :list/title "learn datomic"]]
     (d/db conn))

;; Doing same request as before
;; Using power of pull to separate query from fetched datas
(d/q '[:find (pull ?todo [:todo/gid :todo/description])
       :where
       [?list :list/todo ?todo]
       [?list :list/title "learn datomic"]]
     (d/db conn))

(d/pull (d/db conn)
        ;; The underscore in :list/_todo allows us to search "parents"
        [:todo/description {:list/_todo [:list/title]}]
        [:todo/gid first-id])

;; **Updating entity**

(d/transact conn {:tx-data [{:todo/gid first-id :todo/description "hello, world"}]})

;; **Fetching history**
;; https://docs.datomic.com/cloud/tutorial/history.html#orgee6614f

(->> (d/q '[:find ?tx ?description
            :in $ ?gid
            :where
            ;; op = true means "write"
            [?todo :todo/description ?description ?tx true]
            [?todo :todo/gid ?gid]]
          (d/history (d/db conn))
          first-id)
     (sort-by first)
     (pp/pprint))
