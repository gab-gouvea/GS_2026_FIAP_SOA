package br.com.swarmbuild.dto;

import br.com.swarmbuild.model.enums.TipoRobo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarRoboDTO(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 80) String nome,
        @NotBlank @Size(max = 80) String modelo,
        @NotNull TipoRobo tipo,
        Double latitude,
        Double longitude,
        Double capacidadeCargaKg,
        Double profundidadeMaximaMetros,
        Double capacidadeTransporteKg,
        Double velocidadeMaximaKmh,
        Double precisaoMontagemMm,
        Integer bracosManipuladores
) {
}
