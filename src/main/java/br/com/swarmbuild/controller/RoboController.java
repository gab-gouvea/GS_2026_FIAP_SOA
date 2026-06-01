package br.com.swarmbuild.controller;

import br.com.swarmbuild.dto.CriarRoboDTO;
import br.com.swarmbuild.dto.RoboResponseDTO;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.service.RoboService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/robos")
public class RoboController {

    private final RoboService roboService;

    public RoboController(RoboService roboService) {
        this.roboService = roboService;
    }

    @PostMapping
    public ResponseEntity<RoboResponseDTO> criar(@Valid @RequestBody CriarRoboDTO dto) {
        Robo robo = roboService.criar(dto);
        return ResponseEntity
                .created(URI.create("/api/robos/" + robo.getId()))
                .body(RoboResponseDTO.de(robo));
    }

    @GetMapping
    public List<RoboResponseDTO> listar() {
        return roboService.listar().stream().map(RoboResponseDTO::de).toList();
    }

    @GetMapping("/{id}")
    public RoboResponseDTO buscar(@PathVariable Long id) {
        return RoboResponseDTO.de(roboService.buscar(id));
    }

    @PatchMapping("/{id}/status")
    public RoboResponseDTO atualizarStatus(@PathVariable Long id, @RequestParam StatusRobo status) {
        return RoboResponseDTO.de(roboService.atualizarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        roboService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
