package cl.usm.sansaweigh.repositories;

import cl.usm.sansaweigh.entities.RegistroPesaje;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroPesajeRepository extends MongoRepository<RegistroPesaje, String> {

    //Obtencion de registros filtrando por fecha (createdAt)
    List<RegistroPesaje> findByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);
}
