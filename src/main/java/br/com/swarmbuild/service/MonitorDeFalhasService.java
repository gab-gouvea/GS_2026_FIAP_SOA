package br.com.swarmbuild.service;

import br.com.swarmbuild.model.Alerta;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.Tarefa;
import br.com.swarmbuild.model.enums.SeveridadeAlerta;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoAlerta;
import br.com.swarmbuild.repository.AlertaRepository;
import br.com.swarmbuild.repository.RoboRepository;
import br.com.swarmbuild.repository.TarefaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitorDeFalhasService {

    private static final Logger log = LoggerFactory.getLogger(MonitorDeFalhasService.class);

    private final RoboRepository roboRepository;
    private final TarefaRepository tarefaRepository;
    private final AlertaRepository alertaRepository;
    private final OrquestradorEnxame orquestrador;

    @Value("${swarmbuild.heartbeat.timeout-seconds:60}")
    private long timeoutSegundos;

    public MonitorDeFalhasService(RoboRepository roboRepository,
                                  TarefaRepository tarefaRepository,
                                  AlertaRepository alertaRepository,
                                  OrquestradorEnxame orquestrador) {
        this.roboRepository = roboRepository;
        this.tarefaRepository = tarefaRepository;
        this.alertaRepository = alertaRepository;
        this.orquestrador = orquestrador;
    }

    @Scheduled(fixedRateString = "${swarmbuild.heartbeat.check-rate-ms:10000}")
    @Transactional
    public void detectarRobosOffline() {
        LocalDateTime limite = LocalDateTime.now().minusSeconds(timeoutSegundos);
        List<Robo> offline = roboRepository.findOffline(limite);

        if (offline.isEmpty()) {
            return;
        }

        log.warn("Detectados {} robo(s) offline. Marcando como FALHA e realocando tarefas.", offline.size());

        for (Robo robo : offline) {
            try {
                marcarFalhaERealocarTarefas(robo);
            } catch (Exception ex) {
                log.error("Erro ao tratar falha do robo {}: {}", robo.getCodigo(), ex.getMessage(), ex);
            }
        }
    }

    private void marcarFalhaERealocarTarefas(Robo robo) {
        robo.setStatus(StatusRobo.FALHA);
        roboRepository.save(robo);

        Alerta alerta = new Alerta();
        alerta.setTipo(TipoAlerta.ROBO_OFFLINE);
        alerta.setSeveridade(SeveridadeAlerta.CRITICO);
        alerta.setMensagem("Robo %s nao envia heartbeat ha mais de %ds. Marcado como FALHA."
                .formatted(robo.getCodigo(), timeoutSegundos));
        alerta.setRobo(robo);
        alertaRepository.save(alerta);

        List<Tarefa> tarefas = tarefaRepository.findEmExecucaoPorRobo(robo.getId());
        for (Tarefa t : tarefas) {
            boolean realocada = orquestrador.realocarTarefa(t);
            log.info("Tarefa {} {} apos falha de {}",
                    t.getCodigo(),
                    realocada ? "realocada" : "sem robo disponivel",
                    robo.getCodigo());
        }
    }
}
