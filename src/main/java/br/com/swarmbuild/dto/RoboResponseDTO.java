package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoRobo;

import java.time.LocalDateTime;

public record RoboResponseDTO(
        Long id,
        String codigo,
        String nome,
        String modelo,
        TipoRobo tipo,
        StatusRobo status,
        Integer bateria,
        Double latitude,
        Double longitude,
        LocalDateTime ultimoHeartbeat,
        LocalDateTime criadoEm,
        String descricaoCapacidade
) {
    public static RoboResponseDTO de(Robo robo) {
        return new RoboResponseDTO(
                robo.getId(),
                robo.getCodigo(),
                robo.getNome(),
                robo.getModelo(),
                robo.getTipo(),
                robo.getStatus(),
                robo.getBateria(),
                robo.getCoordenada() != null ? robo.getCoordenada().latitude() : null,
                robo.getCoordenada() != null ? robo.getCoordenada().longitude() : null,
                robo.getUltimoHeartbeat(),
                robo.getCriadoEm(),
                robo.descricaoCapacidade()
        );
    }
}
