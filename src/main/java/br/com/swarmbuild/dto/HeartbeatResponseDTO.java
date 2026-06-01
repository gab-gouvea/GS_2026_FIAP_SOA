package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.Heartbeat;
import br.com.swarmbuild.model.enums.StatusRobo;

import java.time.LocalDateTime;

public record HeartbeatResponseDTO(
        Long id,
        Long roboId,
        String roboCodigo,
        LocalDateTime timestamp,
        Integer bateria,
        Double latitude,
        Double longitude,
        StatusRobo statusReportado,
        String mensagem
) {
    public static HeartbeatResponseDTO de(Heartbeat h) {
        return new HeartbeatResponseDTO(
                h.getId(),
                h.getRobo().getId(),
                h.getRobo().getCodigo(),
                h.getTimestamp(),
                h.getBateria(),
                h.getCoordenada() != null ? h.getCoordenada().latitude() : null,
                h.getCoordenada() != null ? h.getCoordenada().longitude() : null,
                h.getStatusReportado(),
                h.getMensagem()
        );
    }
}
