package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.enums.StatusRobo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HeartbeatDTO(
        @NotNull @Min(0) @Max(100) Integer bateria,
        Double latitude,
        Double longitude,
        @NotNull StatusRobo statusReportado,
        String mensagem
) {
}
