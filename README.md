# Java Performance Microbenchmarking with JMH

Link to the [JMH project](https://github.com/openjdk/jmh).

## How to run

```bash
mvn clean verify
java -jar target/benchmarks.jar
```

## How this was generated

```bash
mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=org.openjdk.jmh \
  -DarchetypeArtifactId=jmh-java-benchmark-archetype \
  -DgroupId=ch.fmartin \
  -DartifactId=microbenchmark \
  -Dversion=1.0
```
