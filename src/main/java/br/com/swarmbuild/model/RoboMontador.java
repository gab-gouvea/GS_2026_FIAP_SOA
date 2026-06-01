package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.TipoRobo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MONTADOR")
@Getter
@Setter
@NoArgsConstructor
public class RoboMontador extends Robo {

    @Column(name = "precisao_mm")
    private Double precisaoMontagemMm;

    @Column(name = "bracos_manipuladores")
    private Integer bracosManipuladores;

    @Override
    public TipoRobo getTipo() {
        return TipoRobo.MONTADOR;
    }

    @Override
    public String descricaoCapacidade() {
        return "Montador: %.2f mm precisao / %d bracos"
                .formatted(precisaoMontagemMm != null ? precisaoMontagemMm : 0.0,
                        bracosManipuladores != null ? bracosManipuladores : 0);
    }
}
