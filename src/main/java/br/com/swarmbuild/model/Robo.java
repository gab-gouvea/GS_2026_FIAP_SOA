package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoRobo;
import br.com.swarmbuild.model.vo.Coordenada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "robos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_robo", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Robo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 80)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRobo status = StatusRobo.DISPONIVEL;

    @Column(nullable = false)
    private Integer bateria = 100;

    @Embedded
    private Coordenada coordenada;

    @Column(name = "ultimo_heartbeat")
    private LocalDateTime ultimoHeartbeat;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusRobo.DISPONIVEL;
        }
        if (this.bateria == null) {
            this.bateria = 100;
        }
    }

    public abstract TipoRobo getTipo();

    public abstract String descricaoCapacidade();

    public boolean estaDisponivel() {
        return this.status == StatusRobo.DISPONIVEL && this.bateria > 10;
    }

    public boolean ehCompativelCom(TipoRobo requerido) {
        return this.getTipo() == requerido;
    }
}
