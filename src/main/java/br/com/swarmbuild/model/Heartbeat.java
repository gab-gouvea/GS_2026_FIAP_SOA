package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.vo.Coordenada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "heartbeats")
@Getter
@Setter
@NoArgsConstructor
public class Heartbeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "robo_id", nullable = false)
    private Robo robo;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Integer bateria;

    @Embedded
    private Coordenada coordenada;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reportado", nullable = false, length = 20)
    private StatusRobo statusReportado;

    @Column(length = 200)
    private String mensagem;

    @PrePersist
    protected void aoCriar() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
