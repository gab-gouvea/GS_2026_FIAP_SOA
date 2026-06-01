package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.Alerta;
import br.com.swarmbuild.model.enums.SeveridadeAlerta;
import br.com.swarmbuild.model.enums.TipoAlerta;

import java.time.LocalDateTime;

public record AlertaResponseDTO(
        Long id,
        TipoAlerta tipo,
        SeveridadeAlerta severidade,
        String mensagem,
        Long roboId,
        String roboCodigo,
        Long tarefaId,
        String tarefaCodigo,
        LocalDateTime criadoEm,
        LocalDateTime resolvidoEm,
        Boolean resolvido
) {
    public static AlertaResponseDTO de(Alerta a) {
        return new AlertaResponseDTO(
                a.getId(),
                a.getTipo(),
                a.getSeveridade(),
                a.getMensagem(),
                a.getRobo() != null ? a.getRobo().getId() : null,
                a.getRobo() != null ? a.getRobo().getCodigo() : null,
                a.getTarefa() != null ? a.getTarefa().getId() : null,
                a.getTarefa() != null ? a.getTarefa().getCodigo() : null,
                a.getCriadoEm(),
                a.getResolvidoEm(),
                a.getResolvido()
        );
    }
}
