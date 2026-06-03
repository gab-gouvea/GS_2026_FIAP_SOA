package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.CriarRoboDTO;
import br.com.swarmbuild.exception.CodigoDuplicadoException;
import br.com.swarmbuild.exception.RegraDeNegocioException;
import br.com.swarmbuild.exception.RoboNaoEncontradoException;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.repository.AlertaRepository;
import br.com.swarmbuild.repository.HeartbeatRepository;
import br.com.swarmbuild.repository.RoboRepository;
import br.com.swarmbuild.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoboService {

    private final RoboRepository roboRepository;
    private final TarefaRepository tarefaRepository;
    private final AlertaRepository alertaRepository;
    private final HeartbeatRepository heartbeatRepository;
    private final OrquestradorEnxame orquestrador;

    public RoboService(RoboRepository roboRepository,
                       TarefaRepository tarefaRepository,
                       AlertaRepository alertaRepository,
                       HeartbeatRepository heartbeatRepository,
                       OrquestradorEnxame orquestrador) {
        this.roboRepository = roboRepository;
        this.tarefaRepository = tarefaRepository;
        this.alertaRepository = alertaRepository;
        this.heartbeatRepository = heartbeatRepository;
        this.orquestrador = orquestrador;
    }

    @Transactional
    public Robo criar(CriarRoboDTO dto) {
        if (roboRepository.existsByCodigo(dto.codigo())) {
            throw new CodigoDuplicadoException(dto.codigo());
        }
        Robo robo = orquestrador.construirRoboAPartirDoDTO(dto);
        return roboRepository.save(robo);
    }

    public List<Robo> listar() {
        return roboRepository.findAll();
    }

    public Robo buscar(Long id) {
        return roboRepository.findById(id)
                .orElseThrow(() -> new RoboNaoEncontradoException(id));
    }

    @Transactional
    public Robo atualizarStatus(Long id, StatusRobo novoStatus) {
        Robo robo = buscar(id);
        robo.setStatus(novoStatus);
        return roboRepository.save(robo);
    }

    @Transactional
    public void deletar(Long id) {
        Robo robo = buscar(id);
        if (robo.getStatus() == StatusRobo.EM_TAREFA) {
            throw new RegraDeNegocioException("Nao e possivel deletar um robo em tarefa");
        }
        if (!tarefaRepository.findEmExecucaoPorRobo(id).isEmpty()) {
            throw new RegraDeNegocioException("Nao e possivel deletar um robo com tarefas ativas atribuidas");
        }
        // Desvincula o historico antes de remover para nao violar as chaves estrangeiras
        // alertas e tarefas concluidas preservam o registro com a referencia ao robo zerada
        //  os heartbeats sao descartados
        tarefaRepository.desvincularRobo(id);
        alertaRepository.desvincularRobo(id);
        heartbeatRepository.deletarPorRobo(id);
        roboRepository.delete(robo);
    }
}
