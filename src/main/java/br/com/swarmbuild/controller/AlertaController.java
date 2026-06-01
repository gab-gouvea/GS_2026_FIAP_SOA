package br.com.swarmbuild.controller;

import br.com.swarmbuild.dto.AlertaResponseDTO;
import br.com.swarmbuild.service.AlertaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public List<AlertaResponseDTO> listar(@RequestParam(required = false) Boolean resolvido) {
        return alertaService.listar(resolvido).stream()
                .map(AlertaResponseDTO::de)
                .toList();
    }

    @GetMapping("/{id}")
    public AlertaResponseDTO buscar(@PathVariable Long id) {
        return AlertaResponseDTO.de(alertaService.buscar(id));
    }

    @PostMapping("/{id}/resolver")
    public AlertaResponseDTO resolver(@PathVariable Long id) {
        return AlertaResponseDTO.de(alertaService.resolver(id));
    }
}
