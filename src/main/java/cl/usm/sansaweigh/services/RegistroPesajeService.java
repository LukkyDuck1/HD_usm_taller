package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.EstadoPesaje;
import cl.usm.sansaweigh.entities.RegistroPesaje;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroPesajeService {
    RegistroPesaje create(RegistroPesaje registro);
    Optional<RegistroPesaje> updateEstado(String id, EstadoPesaje nuevoEstado);
    List<RegistroPesaje> getByDate(LocalDate fecha);
}
