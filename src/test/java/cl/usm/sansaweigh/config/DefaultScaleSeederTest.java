package cl.usm.sansaweigh.config;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultScaleSeederTest {

    @Mock
    private RedisTemplate<String, EspecificacionBalanza> redisTemplate;

    @Mock
    private ValueOperations<String, EspecificacionBalanza> valueOperations;

    @InjectMocks
    private DefaultScaleSeeder seeder;

    @Test
    void run_cuandoNoExisteDefault_loSiembra() {
        when(redisTemplate.hasKey("balanza:-1")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        seeder.run();

        verify(valueOperations).set(eq("balanza:-1"), any(EspecificacionBalanza.class));
    }

    @Test
    void run_cuandoYaExisteDefault_noSiembra() {
        when(redisTemplate.hasKey("balanza:-1")).thenReturn(true);

        seeder.run();

        verify(redisTemplate, never()).opsForValue();
    }
}
