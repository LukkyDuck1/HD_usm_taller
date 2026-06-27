package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.EspecificacionBalanza;

import java.util.Optional;

public interface EspecificacionBalanzaCache {
    Optional<EspecificacionBalanza> get(String id);
    void save(EspecificacionBalanza spec);
}
