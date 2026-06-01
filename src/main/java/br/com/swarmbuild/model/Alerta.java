package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.SeveridadeAlerta;
import br.com.swarmbuild.model.enums.TipoAlerta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@NoArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAlerta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeveridadeAlerta severidade;

    @Column(nullable = false, length = 300)
    private String mensagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robo_id")
    private Robo robo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id")
    private Tarefa tarefa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    @Column(nullable = false)
    private Boolean resolvido = false;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        if (this.resolvido == null) {
            this.resolvido = false;
        }
    }

    public void resolver() {
        this.resolvido = true;
        this.resolvidoEm = LocalDateTime.now();
    }
}
