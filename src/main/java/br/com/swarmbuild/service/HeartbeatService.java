package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.HeartbeatDTO;
import br.com.swarmbuild.model.Alerta;
import br.com.swarmbuild.model.Heartbeat;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.enums.SeveridadeAlerta;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoAlerta;
import br.com.swarmbuild.model.vo.Coordenada;
import br.com.swarmbuild.repository.AlertaRepository;
import br.com.swarmbuild.repository.HeartbeatRepository;
import br.com.swarmbuild.repository.RoboRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HeartbeatService {

    private final HeartbeatRepository heartbeatRepository;
    private final RoboRepository roboRepository;
    private final AlertaRepository alertaRepository;
    private final RoboService roboService;

    @Value("${swarmbuild.bateria.alerta-percentual:20}")
    private int bateriaAlertaPercentual;

    public HeartbeatService(HeartbeatRepository heartbeatRepository,
                            RoboRepository roboRepository,
                            AlertaRepository alertaRepository,
                            RoboService roboService) {
        this.heartbeatRepository = heartbeatRepository;
        this.roboRepository = roboRepository;
        this.alertaRepository = alertaRepository;
        this.roboService = roboService;
    }

    @Transactional
    public Heartbeat registrar(Long roboId, HeartbeatDTO dto) {
        Robo robo = roboService.buscar(roboId);

        robo.setBateria(dto.bateria());
        robo.setUltimoHeartbeat(LocalDateTime.now());
        if (dto.latitude() != null && dto.longitude() != null) {
            robo.setCoordenada(new Coordenada(dto.latitude(), dto.longitude()));
        }
        if (robo.getStatus() == StatusRobo.FALHA && dto.statusReportado() != StatusRobo.FALHA) {
            robo.setStatus(StatusRobo.DISPONIVEL);
        }
        roboRepository.save(robo);

        Heartbeat hb = new Heartbeat();
        hb.setRobo(robo);
        hb.setBateria(dto.bateria());
        hb.setStatusReportado(dto.statusReportado());
        hb.setMensagem(dto.mensagem());
        if (dto.latitude() != null && dto.longitude() != null) {
            hb.setCoordenada(new Coordenada(dto.latitude(), dto.longitude()));
        }
        Heartbeat salvo = heartbeatRepository.save(hb);

        if (dto.bateria() < bateriaAlertaPercentual) {
            Alerta a = new Alerta();
            a.setTipo(TipoAlerta.BATERIA_BAIXA);
            a.setSeveridade(dto.bateria() < 10 ? SeveridadeAlerta.CRITICO : SeveridadeAlerta.AVISO);
            a.setMensagem("Bateria do robo %s em %d%%".formatted(robo.getCodigo(), dto.bateria()));
            a.setRobo(robo);
            alertaRepository.save(a);
        }

        return salvo;
    }

    public List<Heartbeat> historicoDoRobo(Long roboId) {
        roboService.buscar(roboId);
        return heartbeatRepository.findByRoboIdOrderByTimestampDesc(roboId);
    }
}
