package cl.usm.sansaweigh.controllers;

import cl.usm.sansaweigh.entities.EstadoPesaje;
import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.exceptions.IllegalWeighingStateException;
import cl.usm.sansaweigh.services.RegistroPesajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/registros")
public class RegistroPesajeController {

    @Autowired
    private RegistroPesajeService registroPesajeService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid RegistroPesaje registro) {
        try {
            RegistroPesaje res = this.registroPesajeService.create(registro);
            return ResponseEntity.ok(res);
        } catch (IllegalWeighingStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> updateEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            EstadoPesaje nuevoEstado = EstadoPesaje.valueOf(body.get("estado"));
            Optional<RegistroPesaje> res = this.registroPesajeService.updateEstado(id, nuevoEstado);
            if (res.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(res.get());
        } catch (IllegalWeighingStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Estado invalido: " + body.get("estado"));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RegistroPesaje>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            List<RegistroPesaje> registros = this.registroPesajeService.getByDate(fecha);
            return ResponseEntity.ok(registros);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
