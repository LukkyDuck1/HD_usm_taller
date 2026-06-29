package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.CategoriaPeso;
import cl.usm.sansaweigh.entities.EstadoPesaje;
import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.exceptions.IllegalWeighingStateException;
import cl.usm.sansaweigh.repositories.RegistroPesajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistroPesajeServiceImpl implements RegistroPesajeService {

    private static final double SANSA_EN_KG = 1.337;

    @Autowired
    RegistroPesajeRepository registroPesajeRepository;

    @Override
    public RegistroPesaje create(RegistroPesaje registro) {
        double pesoSansas = registro.getPesoKg() / SANSA_EN_KG;
        registro.setPesoSansas(pesoSansas);

        CategoriaPeso categoria = clasificarPeso(pesoSansas);
        registro.setCategoria(categoria);

        validarRestriccionesPesado(registro.getBalanzaId(), categoria);

        registro.setEstado(EstadoPesaje.INGRESADO);
        LocalDateTime ahora = LocalDateTime.now();
        registro.setCreatedAt(ahora);
        registro.setUpdatedAt(ahora);

        return this.registroPesajeRepository.insert(registro);
    }

    @Override
    public Optional<RegistroPesaje> updateEstado(String id, EstadoPesaje nuevoEstado) {
        Optional<RegistroPesaje> encontrado = this.registroPesajeRepository.findById(id);
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }

        RegistroPesaje registro = encontrado.get();
        validarTransicion(registro.getEstado(), nuevoEstado);
        validarRestriccionesPesado(registro.getBalanzaId(), registro.getCategoria());

        registro.setEstado(nuevoEstado);
        registro.setUpdatedAt(LocalDateTime.now());

        return Optional.of(this.registroPesajeRepository.save(registro));
    }

    @Override
    public List<RegistroPesaje> getByDate(LocalDate fecha) {
        return this.registroPesajeRepository.findByCreatedAtBetween(
                fecha.atStartOfDay(), fecha.atTime(23, 59, 59));
    }

    private CategoriaPeso clasificarPeso(double pesoSansas) {
        if (pesoSansas <= 10.0) {
            return CategoriaPeso.LIVIANO;
        } else if (pesoSansas <= 50.0) {
            return CategoriaPeso.MEDIANO;
        }
        return CategoriaPeso.PESADO;
    }

    private void validarRestriccionesPesado(Integer balanzaId, CategoriaPeso categoria) {
        if (categoria != CategoriaPeso.PESADO) {
            return;
        }

        // No se procesan pesados en horario nocturno (20:00 - 06:00)
        int hora = LocalTime.now().getHour();
        if (hora >= 20 || hora < 6) {
            throw new IllegalWeighingStateException(
                    "No se permite procesar paquetes Pesados en horario nocturno (20:00 a 06:00).");
        }

        // Balanza con id primo no acepta pesados en dias impares del mes
        if (balanzaId != null && esPrimo(balanzaId)) {
            int dia = LocalDate.now().getDayOfMonth();
            if (dia % 2 != 0) {
                throw new IllegalWeighingStateException(
                        "La balanza con ID " + balanzaId + " (primo) no puede registrar paquetes Pesados en dias impares.");
            }
        }
    }

    private void validarTransicion(EstadoPesaje actual, EstadoPesaje nuevo) {
        boolean valida = switch (actual) {
            case INGRESADO -> nuevo == EstadoPesaje.PESADO;
            case PESADO -> nuevo == EstadoPesaje.APROBADO || nuevo == EstadoPesaje.RECHAZADO;
            case APROBADO, RECHAZADO -> nuevo == EstadoPesaje.DESPACHADO;
            case DESPACHADO -> false;
        };

        if (!valida) {
            throw new IllegalWeighingStateException(
                    "Transicion de estado no permitida: " + actual + " -> " + nuevo + ".");
        }
    }

    private boolean esPrimo(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
