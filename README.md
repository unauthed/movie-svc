# Restful Movies Service


## Requirements
Docker, Maven and Java 8

## Build and Run

```
docker pull mongo
docker run --name mongo -d -p 27017:27017 mongo
 
mvn clean spring-boot:run

xdg-open http://localhost:8080/api/v1/movie
xdg-open http://localhost:8080/index.html
xdg-open http://localhost:8080/api-guide.html
```

## Docker Compose
```
mvn clean package docker:build

docker-compose up -d

xdg-open http://localhost:9090/api/v1/movie
xdg-open http://localhost:9090/index.html
xdg-open http://localhost:9090/api-guide.html

docker-compose logs -f

```
