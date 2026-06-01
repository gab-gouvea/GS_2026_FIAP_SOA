package br.com.swarmbuild.controller;

import br.com.swarmbuild.dto.HeartbeatDTO;
import br.com.swarmbuild.dto.HeartbeatResponseDTO;
import br.com.swarmbuild.model.Heartbeat;
import br.com.swarmbuild.service.HeartbeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robos/{roboId}/heartbeats")
public class HeartbeatController {

    private final HeartbeatService heartbeatService;

    public HeartbeatController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @PostMapping
    public ResponseEntity<HeartbeatResponseDTO> registrar(@PathVariable Long roboId,
                                                          @Valid @RequestBody HeartbeatDTO dto) {
        Heartbeat hb = heartbeatService.registrar(roboId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(HeartbeatResponseDTO.de(hb));
    }

    @GetMapping
    public List<HeartbeatResponseDTO> historico(@PathVariable Long roboId) {
        return heartbeatService.historicoDoRobo(roboId).stream()
                .map(HeartbeatResponseDTO::de)
                .toList();
    }
}
