package cl.usm.sansaweigh.clients;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import cl.usm.sansaweigh.services.EspecificacionBalanzaCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalScaleClient {

    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 500;

    private final RestClient restClient;

    @Autowired
    private EspecificacionBalanzaCache cache;

    @Value("${external.scale.url}")
    private String externalScaleUrl;

    public ExternalScaleClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public EspecificacionBalanza getScaleSpecifications(String scaleId) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                EspecificacionBalanza spec = restClient.get()
                        .uri(externalScaleUrl + "/" + scaleId)
                        .retrieve()
                        .body(EspecificacionBalanza.class);
                if (spec != null) {
                    cache.save(spec);
                }
                return spec;
            } catch (RestClientException e) {
                attempt++;
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(BASE_DELAY_MS * (1L << (attempt - 1)));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return cache.get(scaleId)
                .orElseGet(() -> cache.get("-1").orElse(null));
    }
}
