package cl.usm.sansaweigh.clients;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExternalScaleClient {

    private final RestClient restClient;

    public ExternalScaleClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    //TODO: consultar especificaciones de la balanza a la API externa.
    //TODO: reintentos exponenciales (max 3) ante errores transitorios de red.
    //TODO: fallback resiliente -> cache Redis -> especificacion por defecto id "-1".
    public EspecificacionBalanza getScaleSpecifications(String scaleId) {
        return null;
    }
}
