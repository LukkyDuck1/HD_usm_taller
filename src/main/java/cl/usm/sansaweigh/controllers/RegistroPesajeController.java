package cl.usm.sansaweigh.controllers;

import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.services.RegistroPesajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registros")
public class RegistroPesajeController {

    @Autowired
    private RegistroPesajeService registroPesajeService;

    //Creacion de registros
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid RegistroPesaje registro) {
        RegistroPesaje res = this.registroPesajeService.create(registro);
        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.internalServerError().build();
    }

    //Actualizacion de registros
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid RegistroPesaje registro) {
        RegistroPesaje res = this.registroPesajeService.update(id, registro);
        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.internalServerError().build();
    }

    //Obtencion de registros filtrando por fecha
    @GetMapping
    public ResponseEntity<List<RegistroPesaje>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<RegistroPesaje> registros = this.registroPesajeService.getByDate(fecha);
        return ResponseEntity.ok(registros);
    }
}
