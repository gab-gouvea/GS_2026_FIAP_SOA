package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.TipoRobo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TRANSPORTADOR")
@Getter
@Setter
@NoArgsConstructor
public class RoboTransportador extends Robo {

    @Column(name = "capacidade_transporte_kg")
    private Double capacidadeTransporteKg;

    @Column(name = "velocidade_maxima_kmh")
    private Double velocidadeMaximaKmh;

    @Override
    public TipoRobo getTipo() {
        return TipoRobo.TRANSPORTADOR;
    }

    @Override
    public String descricaoCapacidade() {
        return "Transportador: %.1f kg / %.1f km/h"
                .formatted(capacidadeTransporteKg != null ? capacidadeTransporteKg : 0.0,
                        velocidadeMaximaKmh != null ? velocidadeMaximaKmh : 0.0);
    }
}
