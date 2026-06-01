package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.TipoRobo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ESCAVADEIRA")
@Getter
@Setter
@NoArgsConstructor
public class RoboEscavadeira extends Robo {

    @Column(name = "capacidade_carga_kg")
    private Double capacidadeCargaKg;

    @Column(name = "profundidade_maxima_m")
    private Double profundidadeMaximaMetros;

    @Override
    public TipoRobo getTipo() {
        return TipoRobo.ESCAVADEIRA;
    }

    @Override
    public String descricaoCapacidade() {
        return "Escavadeira: %.1f kg / %.1f m profundidade"
                .formatted(capacidadeCargaKg != null ? capacidadeCargaKg : 0.0,
                        profundidadeMaximaMetros != null ? profundidadeMaximaMetros : 0.0);
    }
}
