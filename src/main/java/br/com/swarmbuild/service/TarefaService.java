package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.CriarTarefaDTO;
import br.com.swarmbuild.exception.CodigoDuplicadoException;
import br.com.swarmbuild.exception.RegraDeNegocioException;
import br.com.swarmbuild.exception.TarefaNaoEncontradaException;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.Tarefa;
import br.com.swarmbuild.model.enums.PrioridadeTarefa;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.StatusTarefa;
import br.com.swarmbuild.model.vo.Coordenada;
import br.com.swarmbuild.repository.RoboRepository;
import br.com.swarmbuild.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final RoboRepository roboRepository;
    private final OrquestradorEnxame orquestrador;

    public TarefaService(TarefaRepository tarefaRepository,
                         RoboRepository roboRepository,
                         OrquestradorEnxame orquestrador) {
        this.tarefaRepository = tarefaRepository;
        this.roboRepository = roboRepository;
        this.orquestrador = orquestrador;
    }

    @Transactional
    public Tarefa criar(CriarTarefaDTO dto) {
        if (tarefaRepository.existsByCodigo(dto.codigo())) {
            throw new CodigoDuplicadoException(dto.codigo());
        }
        Tarefa t = new Tarefa();
        t.setCodigo(dto.codigo());
        t.setDescricao(dto.descricao());
        t.setTipoRoboRequerido(dto.tipoRoboRequerido());
        t.setPrioridade(dto.prioridade() != null ? dto.prioridade() : PrioridadeTarefa.MEDIA);
        if (dto.latitude() != null && dto.longitude() != null) {
            t.setLocalExecucao(new Coordenada(dto.latitude(), dto.longitude()));
        }
        return tarefaRepository.save(t);
    }

    public List<Tarefa> listar() {
        return tarefaRepository.findAll();
    }

    public Tarefa buscar(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    @Transactional
    public Tarefa atribuirMelhorRobo(Long tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA || tarefa.getStatus() == StatusTarefa.CANCELADA) {
            throw new RegraDeNegocioException("Tarefa ja finalizada");
        }
        Optional<Robo> escolhido = orquestrador.escolherMelhorRobo(tarefa);
        Robo robo = escolhido.orElseThrow(() ->
                new RegraDeNegocioException("Nenhum robo " + tarefa.getTipoRoboRequerido() + " disponivel"));
        robo.setStatus(StatusRobo.EM_TAREFA);
        roboRepository.save(robo);
        tarefa.atribuirA(robo);
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa atribuirRoboEspecifico(Long tarefaId, Long roboId) {
        Tarefa tarefa = buscar(tarefaId);
        Robo robo = roboRepository.findById(roboId)
                .orElseThrow(() -> new RegraDeNegocioException("Robo nao encontrado: " + roboId));
        if (!robo.ehCompativelCom(tarefa.getTipoRoboRequerido())) {
            throw new RegraDeNegocioException("Robo " + robo.getCodigo() + " nao e compativel com tarefa que exige " + tarefa.getTipoRoboRequerido());
        }
        if (!robo.estaDisponivel()) {
            throw new RegraDeNegocioException("Robo " + robo.getCodigo() + " nao esta disponivel");
        }
        robo.setStatus(StatusRobo.EM_TAREFA);
        roboRepository.save(robo);
        tarefa.atribuirA(robo);
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa concluir(Long tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        if (!tarefa.estaEmExecucao()) {
            throw new RegraDeNegocioException("Tarefa nao esta em execucao");
        }
        Robo robo = tarefa.getRoboAtribuido();
        if (robo != null) {
            robo.setStatus(StatusRobo.DISPONIVEL);
            roboRepository.save(robo);
        }
        tarefa.concluir();
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa realocar(Long tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        if (tarefa.getStatus() != StatusTarefa.EM_EXECUCAO && tarefa.getStatus() != StatusTarefa.REALOCADA) {
            throw new RegraDeNegocioException("Tarefa precisa estar em execucao para ser realocada");
        }
        orquestrador.realocarTarefa(tarefa);
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public void deletar(Long id) {
        Tarefa t = buscar(id);
        if (t.estaEmExecucao()) {
            throw new RegraDeNegocioException("Nao e possivel deletar tarefa em execucao");
        }
        tarefaRepository.delete(t);
    }
}
