# Tiffany

Tiffany provides GeoTiff generation from lcmap-gaia outputs.

## Building the jar

Tiffany is a Clojure project built with Leiningen. 

```
lein uberjar
```

## Creating a product

Define the input file location, and the output file name

```
java -jar target/tiffany-0.1.0-SNAPSHOT-standalone.jar curve-fit.json curve-fit.tif
```

## Running tests

```
make runtests
```
