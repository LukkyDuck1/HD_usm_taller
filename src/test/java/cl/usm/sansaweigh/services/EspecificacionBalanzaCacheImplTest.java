package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EspecificacionBalanzaCacheImplTest {

    @Mock
    private RedisTemplate<String, EspecificacionBalanza> redisTemplate;

    @Mock
    private ValueOperations<String, EspecificacionBalanza> valueOperations;

    @InjectMocks
    private EspecificacionBalanzaCacheImpl cache;

    @Test
    void get_existente_retornaEspecificacion() {
        EspecificacionBalanza spec = new EspecificacionBalanza("5", "Balanza", "Marca", 100, 0.1, 0.0);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("balanza:5")).thenReturn(spec);

        Optional<EspecificacionBalanza> res = cache.get("5");

        assertThat(res).contains(spec);
    }

    @Test
    void get_inexistente_retornaVacio() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("balanza:99")).thenReturn(null);

        Optional<EspecificacionBalanza> res = cache.get("99");

        assertThat(res).isEmpty();
    }

    @Test
    void save_guardaConTtlDe120Segundos() {
        EspecificacionBalanza spec = new EspecificacionBalanza("5", "Balanza", "Marca", 100, 0.1, 0.0);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cache.save(spec);

        verify(valueOperations).set("balanza:5", spec, Duration.ofSeconds(120));
    }
}
