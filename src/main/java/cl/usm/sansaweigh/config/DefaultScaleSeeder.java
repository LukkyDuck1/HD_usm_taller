package cl.usm.sansaweigh.config;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultScaleSeeder implements CommandLineRunner {

    private static final String DEFAULT_KEY = "balanza:-1";

    @Autowired
    RedisTemplate<String, EspecificacionBalanza> redisTemplate;

    @Override
    public void run(String... args) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(DEFAULT_KEY))) {
            EspecificacionBalanza porDefecto = new EspecificacionBalanza(
                    "-1", "Balanza por defecto", "Generica", 0.0, 0.0, 0.0);
            this.redisTemplate.opsForValue().set(DEFAULT_KEY, porDefecto);
        }
    }
}
