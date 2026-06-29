package cl.usm.sansaweigh.clients;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import cl.usm.sansaweigh.services.EspecificacionBalanzaCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class ExternalScaleClientTest {

    @Mock
    private EspecificacionBalanzaCache cache;

    private MockRestServiceServer mockServer;
    private ExternalScaleClient client;

    private static final String BASE_URL = "http://test-scales.local/scales";
    private static final String SCALE_ID = "sc-01";

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        client = new ExternalScaleClient(builder);
        ReflectionTestUtils.setField(client, "cache", cache);
        ReflectionTestUtils.setField(client, "externalScaleUrl", BASE_URL);
    }

    @Test
    void getScaleSpecifications_respuestaOk_retornaEspecificacionYGuardaEnCache() {
        mockServer.expect(requestTo(BASE_URL + "/" + SCALE_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":"sc-01","name":"Balanza A","brand":"Acme",
                         "maxCapacity":100.0,"precision":0.01,"lastCalibrationOffset":0.002}
                        """, MediaType.APPLICATION_JSON));

        EspecificacionBalanza result = client.getScaleSpecifications(SCALE_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("sc-01");
        assertThat(result.getName()).isEqualTo("Balanza A");
        verify(cache).save(result);
        mockServer.verify();
    }

    @Test
    void getScaleSpecifications_errorHttp_agotaReintentosCaeACache() {
        for (int i = 0; i < 3; i++) {
            mockServer.expect(requestTo(BASE_URL + "/" + SCALE_ID))
                    .andRespond(withServerError());
        }

        EspecificacionBalanza cached = new EspecificacionBalanza("sc-01", "Cached", "Brand", 50, 0.1, 0.0);
        when(cache.get(SCALE_ID)).thenReturn(Optional.of(cached));

        EspecificacionBalanza result = client.getScaleSpecifications(SCALE_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("sc-01");
        verify(cache, never()).save(any());
        verify(cache).get(SCALE_ID);
        mockServer.verify();
    }

    @Test
    void getScaleSpecifications_agotaReintentosYCacheVacia_retornaEspecificacionDefault() {
        for (int i = 0; i < 3; i++) {
            mockServer.expect(requestTo(BASE_URL + "/" + SCALE_ID))
                    .andRespond(withServerError());
        }

        EspecificacionBalanza defaultSpec = new EspecificacionBalanza("-1", "Default", "N/A", 0, 0, 0);
        when(cache.get(SCALE_ID)).thenReturn(Optional.empty());
        when(cache.get("-1")).thenReturn(Optional.of(defaultSpec));

        EspecificacionBalanza result = client.getScaleSpecifications(SCALE_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("-1");
        verify(cache).get(SCALE_ID);
        verify(cache).get("-1");
        mockServer.verify();
    }

    @Test
    void getScaleSpecifications_cacheHit_retornaDesdeCacheAlFallarApi() {
        for (int i = 0; i < 3; i++) {
            mockServer.expect(requestTo(BASE_URL + "/" + SCALE_ID))
                    .andRespond(withServerError());
        }

        EspecificacionBalanza cached = new EspecificacionBalanza("sc-01", "From Cache", "Brand", 75, 0.05, 0.001);
        when(cache.get(eq(SCALE_ID))).thenReturn(Optional.of(cached));

        EspecificacionBalanza result = client.getScaleSpecifications(SCALE_ID);

        assertThat(result.getName()).isEqualTo("From Cache");
        verify(cache, never()).save(any());
        mockServer.verify();
    }
}
