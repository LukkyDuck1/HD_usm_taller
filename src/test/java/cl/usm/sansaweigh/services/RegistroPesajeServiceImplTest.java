package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.CategoriaPeso;
import cl.usm.sansaweigh.entities.EstadoPesaje;
import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.exceptions.IllegalWeighingStateException;
import cl.usm.sansaweigh.repositories.RegistroPesajeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroPesajeServiceImplTest {

    @Mock
    private RegistroPesajeRepository repository;

    @InjectMocks
    private RegistroPesajeServiceImpl service;

    private RegistroPesaje nuevo(Integer balanzaId, double pesoKg) {
        RegistroPesaje r = new RegistroPesaje();
        r.setBalanzaId(balanzaId);
        r.setPaqueteId("PKG-1");
        r.setPesoKg(pesoKg);
        return r;
    }

    @Test
    void create_convierteKgASansasYClasificaLiviano() {
        when(repository.insert(any(RegistroPesaje.class))).thenAnswer(i -> i.getArgument(0));

        // 13.37 kg / 1.337 = 10 Sansas
        RegistroPesaje res = service.create(nuevo(100, 13.37));

        assertThat(res.getPesoSansas()).isCloseTo(10.0, within(0.0001));
        assertThat(res.getCategoria()).isEqualTo(CategoriaPeso.LIVIANO);
        assertThat(res.getEstado()).isEqualTo(EstadoPesaje.INGRESADO);
        assertThat(res.getCreatedAt()).isNotNull();
        assertThat(res.getUpdatedAt()).isNotNull();
        verify(repository).insert(res);
    }

    @Test
    void create_clasificaMedianoEnElBorde() {
        when(repository.insert(any(RegistroPesaje.class))).thenAnswer(i -> i.getArgument(0));

        // 66.85 kg / 1.337 = 50 Sansas (limite mediano)
        RegistroPesaje res = service.create(nuevo(100, 66.85));

        assertThat(res.getPesoSansas()).isCloseTo(50.0, within(0.0001));
        assertThat(res.getCategoria()).isEqualTo(CategoriaPeso.MEDIANO);
    }

    @Test
    void create_pesadoEnHorarioDiurnoConBalanzaNoPrima() {
        LocalTime diurno = LocalTime.of(10, 0);
        LocalDate diaImpar = LocalDate.of(2026, 6, 3);
        when(repository.insert(any(RegistroPesaje.class))).thenAnswer(i -> i.getArgument(0));

        try (MockedStatic<LocalTime> lt = mockStatic(LocalTime.class);
             MockedStatic<LocalDate> ld = mockStatic(LocalDate.class)) {
            lt.when(LocalTime::now).thenReturn(diurno);
            ld.when(LocalDate::now).thenReturn(diaImpar);

            // balanza 100 no es prima -> permitido aunque sea dia impar
            RegistroPesaje res = service.create(nuevo(100, 100.0));

            assertThat(res.getCategoria()).isEqualTo(CategoriaPeso.PESADO);
        }
    }

    @Test
    void create_pesadoEnHorarioNocturnoLanzaExcepcion() {
        LocalTime nocturno = LocalTime.of(22, 0);

        try (MockedStatic<LocalTime> lt = mockStatic(LocalTime.class)) {
            lt.when(LocalTime::now).thenReturn(nocturno);

            assertThatThrownBy(() -> service.create(nuevo(100, 100.0)))
                    .isInstanceOf(IllegalWeighingStateException.class);
        }
        verify(repository, never()).insert(any(RegistroPesaje.class));
    }

    @Test
    void create_pesadoBalanzaPrimaEnDiaImparLanzaExcepcion() {
        LocalTime diurno = LocalTime.of(10, 0);
        LocalDate diaImpar = LocalDate.of(2026, 6, 3);

        try (MockedStatic<LocalTime> lt = mockStatic(LocalTime.class);
             MockedStatic<LocalDate> ld = mockStatic(LocalDate.class)) {
            lt.when(LocalTime::now).thenReturn(diurno);
            ld.when(LocalDate::now).thenReturn(diaImpar);

            // 101 es primo y el dia es impar
            assertThatThrownBy(() -> service.create(nuevo(101, 100.0)))
                    .isInstanceOf(IllegalWeighingStateException.class);
        }
        verify(repository, never()).insert(any(RegistroPesaje.class));
    }

    @Test
    void create_pesadoBalanzaPrimaEnDiaParEsPermitido() {
        LocalTime diurno = LocalTime.of(10, 0);
        LocalDate diaPar = LocalDate.of(2026, 6, 4);
        when(repository.insert(any(RegistroPesaje.class))).thenAnswer(i -> i.getArgument(0));

        try (MockedStatic<LocalTime> lt = mockStatic(LocalTime.class);
             MockedStatic<LocalDate> ld = mockStatic(LocalDate.class)) {
            lt.when(LocalTime::now).thenReturn(diurno);
            ld.when(LocalDate::now).thenReturn(diaPar);

            RegistroPesaje res = service.create(nuevo(101, 100.0));

            assertThat(res.getCategoria()).isEqualTo(CategoriaPeso.PESADO);
        }
    }

    @Test
    void updateEstado_registroInexistenteDevuelveVacio() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        Optional<RegistroPesaje> res = service.updateEstado("x", EstadoPesaje.PESADO);

        assertThat(res).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void updateEstado_transicionValida() {
        RegistroPesaje r = new RegistroPesaje();
        r.setId("1");
        r.setEstado(EstadoPesaje.INGRESADO);
        r.setCategoria(CategoriaPeso.LIVIANO);
        when(repository.findById("1")).thenReturn(Optional.of(r));
        when(repository.save(any(RegistroPesaje.class))).thenAnswer(i -> i.getArgument(0));

        Optional<RegistroPesaje> res = service.updateEstado("1", EstadoPesaje.PESADO);

        assertThat(res).isPresent();
        assertThat(res.get().getEstado()).isEqualTo(EstadoPesaje.PESADO);
        assertThat(res.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void updateEstado_transicionInvalidaLanzaExcepcion() {
        RegistroPesaje r = new RegistroPesaje();
        r.setId("1");
        r.setEstado(EstadoPesaje.INGRESADO);
        r.setCategoria(CategoriaPeso.LIVIANO);
        when(repository.findById("1")).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.updateEstado("1", EstadoPesaje.APROBADO))
                .isInstanceOf(IllegalWeighingStateException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void getByDate_consultaPorRangoDelDia() {
        LocalDate fecha = LocalDate.of(2026, 6, 27);
        RegistroPesaje r = new RegistroPesaje();
        when(repository.findByCreatedAtBetween(
                fecha.atStartOfDay(), fecha.atTime(23, 59, 59)))
                .thenReturn(List.of(r));

        List<RegistroPesaje> res = service.getByDate(fecha);

        assertThat(res).hasSize(1);
        verify(repository).findByCreatedAtBetween(
                fecha.atStartOfDay(), fecha.atTime(23, 59, 59));
    }
}
