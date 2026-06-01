package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.Tarefa;
import br.com.swarmbuild.model.enums.PrioridadeTarefa;
import br.com.swarmbuild.model.enums.StatusTarefa;
import br.com.swarmbuild.model.enums.TipoRobo;

import java.time.LocalDateTime;

public record TarefaResponseDTO(
        Long id,
        String codigo,
        String descricao,
        TipoRobo tipoRoboRequerido,
        PrioridadeTarefa prioridade,
        StatusTarefa status,
        Long roboAtribuidoId,
        String roboAtribuidoCodigo,
        Double latitude,
        Double longitude,
        LocalDateTime criadaEm,
        LocalDateTime iniciadaEm,
        LocalDateTime concluidaEm,
        Integer vezesRealocada
) {
    public static TarefaResponseDTO de(Tarefa t) {
        return new TarefaResponseDTO(
                t.getId(),
                t.getCodigo(),
                t.getDescricao(),
                t.getTipoRoboRequerido(),
                t.getPrioridade(),
                t.getStatus(),
                t.getRoboAtribuido() != null ? t.getRoboAtribuido().getId() : null,
                t.getRoboAtribuido() != null ? t.getRoboAtribuido().getCodigo() : null,
                t.getLocalExecucao() != null ? t.getLocalExecucao().latitude() : null,
                t.getLocalExecucao() != null ? t.getLocalExecucao().longitude() : null,
                t.getCriadaEm(),
                t.getIniciadaEm(),
                t.getConcluidaEm(),
                t.getVezesRealocada()
        );
    }
}
