package cl.usm.sansaweigh.controllers;

import cl.usm.sansaweigh.entities.CategoriaPeso;
import cl.usm.sansaweigh.entities.EstadoPesaje;
import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.exceptions.IllegalWeighingStateException;
import cl.usm.sansaweigh.services.RegistroPesajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroPesajeController.class)
class RegistroPesajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistroPesajeService registroPesajeService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistroPesaje registroEjemplo() {
        RegistroPesaje r = new RegistroPesaje();
        r.setId("abc123");
        r.setBalanzaId(2);
        r.setPaqueteId("PKG-001");
        r.setPesoSansas(4.11);
        r.setCategoria(CategoriaPeso.LIVIANO);
        r.setEstado(EstadoPesaje.INGRESADO);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    void create_payload_valido_retorna200() throws Exception {
        RegistroPesaje registro = registroEjemplo();
        when(registroPesajeService.create(any())).thenReturn(registro);

        Map<String, Object> body = Map.of(
                "balanzaId", 2,
                "paqueteId", "PKG-001",
                "pesoKg", 5.5
        );

        mockMvc.perform(post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.estado").value("INGRESADO"));
    }

    @Test
    void create_transicionInvalida_retorna400() throws Exception {
        when(registroPesajeService.create(any()))
                .thenThrow(new IllegalWeighingStateException("No se permite procesar paquetes Pesados en horario nocturno."));

        Map<String, Object> body = Map.of(
                "balanzaId", 2,
                "paqueteId", "PKG-002",
                "pesoKg", 100.0
        );

        mockMvc.perform(post("/registros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEstado_idInexistente_retorna404() throws Exception {
        when(registroPesajeService.updateEstado(eq("noexiste"), any()))
                .thenReturn(Optional.empty());

        Map<String, String> body = Map.of("estado", "PESADO");

        mockMvc.perform(put("/registros/noexiste/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEstado_transicionInvalida_retorna400() throws Exception {
        when(registroPesajeService.updateEstado(eq("abc123"), eq(EstadoPesaje.DESPACHADO)))
                .thenThrow(new IllegalWeighingStateException("Transicion de estado no permitida: INGRESADO -> DESPACHADO."));

        Map<String, String> body = Map.of("estado", "DESPACHADO");

        mockMvc.perform(put("/registros/abc123/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByDate_retornaListaFiltrada() throws Exception {
        RegistroPesaje registro = registroEjemplo();
        when(registroPesajeService.getByDate(eq(LocalDate.of(2026, 6, 28))))
                .thenReturn(List.of(registro));

        mockMvc.perform(get("/registros")
                        .param("fecha", "2026-06-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("abc123"))
                .andExpect(jsonPath("$[0].categoria").value("LIVIANO"));
    }
}
