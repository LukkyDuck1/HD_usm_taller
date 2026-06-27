# Configuracion del entorno

## Requisitos

- Java 25 (JDK)
- Maven (se incluye el wrapper `mvnw`)
- MongoDB en `localhost:27017`
- Redis en `localhost:6379`

## Propiedades

`src/main/resources/application.properties`:

```
spring.application.name=sansaweigh
spring.mongodb.uri=mongodb://localhost:27017/sansaweigh
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Levantar el proyecto

```
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Ejecutar las pruebas

```
./mvnw test
```

## Ver esta documentacion

La documentacion usa Docsify. Para verla en local:

```
npm i -g docsify-cli
docsify serve docs
```

Queda en `http://localhost:3000`.
