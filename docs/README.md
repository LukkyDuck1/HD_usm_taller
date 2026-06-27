# SansaWeigh

Microservicio para gestionar estaciones de pesaje de paquetes de la empresa de logistica
SansaWeigh. Clasifica paquetes por peso, controla las reglas de procesamiento, persiste el
historial en MongoDB, cachea las especificaciones de balanzas en Redis y se integra con un
registro externo de especificaciones.

## Contenido

- [Manual de usuario](manual-usuario.md): endpoints y ejemplos de uso.
- [Configuracion del entorno](configuracion-entorno.md): requisitos y como levantar el proyecto.
- [Arquitectura](arquitectura.md): capas, tecnologias y flujo del sistema.
- [API (Swagger)](api.md): especificacion OpenAPI.

## Tecnologias

- Java 25, Spring Boot 4.x
- Spring Web, Spring Data MongoDB, Spring Data Redis, RestClient
- Lombok
- JUnit 5, Mockito, AssertJ
