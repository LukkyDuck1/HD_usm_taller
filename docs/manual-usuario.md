# Manual de usuario

La API expone la gestion de registros de pesaje bajo el recurso `/registros`.

## Unidad de medida

El sistema no trabaja en kilogramos. El peso se expresa en **Sansas**:

> 1 Sansa = 1.337 kg

El peso de entrada se envia en kilogramos (`pesoKg`) y el sistema lo convierte a Sansas.

## Clasificacion por peso

| Categoria | Rango (Sansas) |
|---|---|
| LIVIANO | hasta 10 |
| MEDIANO | mas de 10 y hasta 50 |
| PESADO | mas de 50 |

## Estados del pesaje

`INGRESADO` -> `PESADO` -> `APROBADO` o `RECHAZADO` -> `DESPACHADO`

Una transicion no permitida devuelve HTTP 400.

## Restricciones para paquetes Pesados

- No se procesan entre las 20:00 y las 06:00.
- Una balanza con id primo no acepta pesados en dias calendario impares.

## Endpoints

### Crear registro

```
POST /registros
Content-Type: application/json

{
  "balanzaId": 101,
  "paqueteId": "PKG-001",
  "pesoKg": 20.0
}
```

Crea el registro en estado `INGRESADO`, convierte el peso a Sansas y asigna la categoria.

### Cambiar estado

```
PUT /registros/{id}/estado?estado=PESADO
```

Valida la transicion segun la maquina de estados.

### Listar por fecha

```
GET /registros?fecha=2026-06-27
```

Devuelve los registros creados en la fecha indicada.

## Codigos de respuesta

| Codigo | Significado |
|---|---|
| 200 | Operacion correcta |
| 400 | Transicion invalida o restriccion de negocio |
| 404 | Registro no encontrado |
| 500 | Error interno |
