package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.enums.PrioridadeTarefa;
import br.com.swarmbuild.model.enums.TipoRobo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarTarefaDTO(
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 200) String descricao,
        @NotNull TipoRobo tipoRoboRequerido,
        PrioridadeTarefa prioridade,
        Double latitude,
        Double longitude
) {
}
