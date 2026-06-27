# Arquitectura

Proyecto Spring Boot organizado en capas, package base `cl.usm.sansaweigh`.

## Capas

| Paquete | Responsabilidad |
|---|---|
| `entities` | Documentos de MongoDB y modelos (`RegistroPesaje`, `EspecificacionBalanza`) y enums (`CategoriaPeso`, `EstadoPesaje`) |
| `repositories` | Acceso a datos con Spring Data MongoDB |
| `services` | Logica de negocio (interfaz + implementacion) |
| `controllers` | Endpoints REST |
| `clients` | Integracion con la API externa de balanzas |
| `exceptions` | Excepciones de negocio |
| `config` | Configuracion de Redis |

## Persistencia (MongoDB)

`RegistroPesaje` guarda id de balanza, id de paquete, peso en Sansas, categoria, estado y
las marcas de tiempo (`createdAt`, `updatedAt`).

## Cache (Redis)

Las especificaciones de balanza se cachean con un TTL de 120 segundos para evitar consultas
repetidas a la API externa. Existe una especificacion por defecto con id `-1` usada como
respaldo.

## Integracion externa

`ExternalScaleClient` consulta las especificaciones de una balanza a una API externa. Ante
errores transitorios reintenta (con backoff). Si la API no responde, usa la version cacheada;
si tampoco existe, recurre a la especificacion por defecto (`-1`).

## Flujo de un pesaje

1. Se recibe el peso en kilogramos y se convierte a Sansas.
2. Se clasifica el paquete (LIVIANO / MEDIANO / PESADO).
3. Se validan las restricciones de procesamiento.
4. Se crea el registro en estado `INGRESADO`.
5. Las transiciones de estado siguen la maquina de estados definida.
