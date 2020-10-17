# Datomic Tutorial

## Local Dev Setup

Since Datomic team delivered "dev-local", life is easy for Datomic playground.

Follow the [official guide](https://docs.datomic.com/cloud/dev-local.html) and you're done !

## Small tutorial by [Drew Verlee](https://github.com/drewverlee)

At the moment, two parts :
- [Add data, Basic Querying](https://drewverlee.github.io/posts-output/2020-4-13-learn-datomic-part-1.html)
- [Pull Querying](https://drewverlee.github.io/posts-output/2020-4-18-learn-datomic-part-2.html)

## Lessons

First lesson I learned from Datomic, is that you should not rely on `:db/id` to identify an entity, consider it as internal information. Create some `:entity/gid` with a uuid.
