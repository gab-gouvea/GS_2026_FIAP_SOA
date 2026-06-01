package br.com.swarmbuild.controller;

import br.com.swarmbuild.dto.CriarTarefaDTO;
import br.com.swarmbuild.dto.TarefaResponseDTO;
import br.com.swarmbuild.model.Tarefa;
import br.com.swarmbuild.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@Valid @RequestBody CriarTarefaDTO dto) {
        Tarefa t = tarefaService.criar(dto);
        return ResponseEntity
                .created(URI.create("/api/tarefas/" + t.getId()))
                .body(TarefaResponseDTO.de(t));
    }

    @GetMapping
    public List<TarefaResponseDTO> listar() {
        return tarefaService.listar().stream().map(TarefaResponseDTO::de).toList();
    }

    @GetMapping("/{id}")
    public TarefaResponseDTO buscar(@PathVariable Long id) {
        return TarefaResponseDTO.de(tarefaService.buscar(id));
    }

    @PostMapping("/{id}/atribuir")
    public TarefaResponseDTO atribuirMelhorRobo(@PathVariable Long id) {
        return TarefaResponseDTO.de(tarefaService.atribuirMelhorRobo(id));
    }

    @PostMapping("/{id}/atribuir/{roboId}")
    public TarefaResponseDTO atribuirEspecifico(@PathVariable Long id, @PathVariable Long roboId) {
        return TarefaResponseDTO.de(tarefaService.atribuirRoboEspecifico(id, roboId));
    }

    @PostMapping("/{id}/concluir")
    public TarefaResponseDTO concluir(@PathVariable Long id) {
        return TarefaResponseDTO.de(tarefaService.concluir(id));
    }

    @PostMapping("/{id}/realocar")
    public TarefaResponseDTO realocar(@PathVariable Long id) {
        return TarefaResponseDTO.de(tarefaService.realocar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
