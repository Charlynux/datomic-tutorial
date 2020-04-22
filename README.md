# Datomic Tutorial

## Local Dev Setup

It's may be easier to create an instance on AWS, but for development, I find easier to have all on my machine.

Official documentation is OK for it.

https://docs.datomic.com/on-prem/dev-setup.html

You finish by running 3 terminals in parallel.

### Transactor

```cmd
bin/transactor config/dev-transactor-template.properties
```

### Peer Server

```cmd
bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d iteracode,datomic:dev://localhost:4334/iteracode
```

(Here `iteracode` is my database name.)

### Console

```cmd
bin/transactor config/dev-transactor-template.properties
```

Optional, but I'm happy to rely on some GUI when I don't know exactly what I'm looking for.

## Small tutorial by [Drew Verlee](https://github.com/drewverlee)

At the moment, two parts :
- [Add data, Basic Querying](https://drewverlee.github.io/posts-output/2020-4-13-learn-datomic-part-1.html)
- [Pull Querying](https://drewverlee.github.io/posts-output/2020-4-18-learn-datomic-part-2.html)

## Experiences

To explore more of the immutable part, I did some experiences by myself.

First lesson I learned, is that you should not rely on `:db/id` to identify an entity, consider it as internal information. Create some `:entity/gid` with a uuid.

