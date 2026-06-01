package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.PrioridadeTarefa;
import br.com.swarmbuild.model.enums.StatusTarefa;
import br.com.swarmbuild.model.enums.TipoRobo;
import br.com.swarmbuild.model.vo.Coordenada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
@NoArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_robo_requerido", nullable = false, length = 20)
    private TipoRobo tipoRoboRequerido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeTarefa prioridade = PrioridadeTarefa.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTarefa status = StatusTarefa.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robo_atribuido_id")
    private Robo roboAtribuido;

    @Embedded
    private Coordenada localExecucao;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @Column(name = "iniciada_em")
    private LocalDateTime iniciadaEm;

    @Column(name = "concluida_em")
    private LocalDateTime concluidaEm;

    @Column(name = "vezes_realocada", nullable = false)
    private Integer vezesRealocada = 0;

    @PrePersist
    protected void aoCriar() {
        this.criadaEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusTarefa.PENDENTE;
        }
        if (this.vezesRealocada == null) {
            this.vezesRealocada = 0;
        }
    }

    public void atribuirA(Robo robo) {
        this.roboAtribuido = robo;
        this.status = StatusTarefa.EM_EXECUCAO;
        this.iniciadaEm = LocalDateTime.now();
    }

    public void desatribuir() {
        this.roboAtribuido = null;
        this.status = StatusTarefa.PENDENTE;
    }

    public void marcarRealocada() {
        this.status = StatusTarefa.REALOCADA;
        this.vezesRealocada = (this.vezesRealocada == null ? 0 : this.vezesRealocada) + 1;
    }

    public void concluir() {
        this.status = StatusTarefa.CONCLUIDA;
        this.concluidaEm = LocalDateTime.now();
    }

    public boolean estaEmExecucao() {
        return this.status == StatusTarefa.EM_EXECUCAO || this.status == StatusTarefa.REALOCADA;
    }
}
