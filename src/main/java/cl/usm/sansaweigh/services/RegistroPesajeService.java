package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.RegistroPesaje;

import java.time.LocalDate;
import java.util.List;

public interface RegistroPesajeService {
    RegistroPesaje create(RegistroPesaje registro);
    RegistroPesaje update(String id, RegistroPesaje registro);
    List<RegistroPesaje> getByDate(LocalDate fecha);
}
