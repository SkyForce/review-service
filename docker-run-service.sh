#!/bin/bash

./mvnw clean package -DskipTests
POM_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

docker build --build-arg JAR_PATH=target/review-service-"$POM_VERSION"-jar-with-dependencies.jar \
  -t nshigarov/review-service:"$POM_VERSION" \
  -t nshigarov/review-service:latest \
  .

docker-compose down -v

docker network create product-review-network
docker-compose up

