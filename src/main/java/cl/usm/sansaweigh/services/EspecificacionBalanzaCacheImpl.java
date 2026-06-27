package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class EspecificacionBalanzaCacheImpl implements EspecificacionBalanzaCache {

    private static final String KEY_PREFIX = "balanza:";
    private static final Duration TTL = Duration.ofSeconds(120);

    @Autowired
    RedisTemplate<String, EspecificacionBalanza> redisTemplate;

    @Override
    public Optional<EspecificacionBalanza> get(String id) {
        return Optional.ofNullable(this.redisTemplate.opsForValue().get(KEY_PREFIX + id));
    }

    @Override
    public void save(EspecificacionBalanza spec) {
        this.redisTemplate.opsForValue().set(KEY_PREFIX + spec.getId(), spec, TTL);
    }
}
