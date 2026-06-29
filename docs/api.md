# API (Swagger)

La especificación completa de la API está en formato OpenAPI 3.1 en [`docs/openapi.yaml`](openapi.yaml).

## Swagger UI

<div id="swagger-ui"></div>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui.css">
<script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
<script>
  document.addEventListener('DOMContentLoaded', function () {
    SwaggerUIBundle({
      url: 'openapi.yaml',
      dom_id: '#swagger-ui',
      presets: [SwaggerUIBundle.presets.apis, SwaggerUIBundle.SwaggerUIStandalonePreset],
      layout: 'BaseLayout',
      deepLinking: true
    });
  });
</script>
