package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.CriarRoboDTO;
import br.com.swarmbuild.model.*;
import br.com.swarmbuild.model.enums.SeveridadeAlerta;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoAlerta;
import br.com.swarmbuild.model.vo.Coordenada;
import br.com.swarmbuild.repository.AlertaRepository;
import br.com.swarmbuild.repository.RoboRepository;
import br.com.swarmbuild.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class OrquestradorEnxameImpl implements OrquestradorEnxame {

    private final RoboRepository roboRepository;
    private final TarefaRepository tarefaRepository;
    private final AlertaRepository alertaRepository;

    public OrquestradorEnxameImpl(RoboRepository roboRepository,
                                  TarefaRepository tarefaRepository,
                                  AlertaRepository alertaRepository) {
        this.roboRepository = roboRepository;
        this.tarefaRepository = tarefaRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public Robo construirRoboAPartirDoDTO(CriarRoboDTO dto) {
        Robo robo = switch (dto.tipo()) {
            case ESCAVADEIRA -> {
                RoboEscavadeira e = new RoboEscavadeira();
                e.setCapacidadeCargaKg(dto.capacidadeCargaKg());
                e.setProfundidadeMaximaMetros(dto.profundidadeMaximaMetros());
                yield e;
            }
            case TRANSPORTADOR -> {
                RoboTransportador t = new RoboTransportador();
                t.setCapacidadeTransporteKg(dto.capacidadeTransporteKg());
                t.setVelocidadeMaximaKmh(dto.velocidadeMaximaKmh());
                yield t;
            }
            case MONTADOR -> {
                RoboMontador m = new RoboMontador();
                m.setPrecisaoMontagemMm(dto.precisaoMontagemMm());
                m.setBracosManipuladores(dto.bracosManipuladores());
                yield m;
            }
        };
        robo.setCodigo(dto.codigo());
        robo.setNome(dto.nome());
        robo.setModelo(dto.modelo());
        if (dto.latitude() != null && dto.longitude() != null) {
            robo.setCoordenada(new Coordenada(dto.latitude(), dto.longitude()));
        }
        return robo;
    }

    @Override
    public Optional<Robo> escolherMelhorRobo(Tarefa tarefa) {
        List<Robo> candidatos = roboRepository.findDisponiveisPorTipo(tarefa.getTipoRoboRequerido())
                .stream()
                .filter(Robo::estaDisponivel)
                .toList();

        if (candidatos.isEmpty()) {
            return Optional.empty();
        }

        Coordenada destino = tarefa.getLocalExecucao();
        if (destino == null) {
            return candidatos.stream().max(Comparator.comparing(Robo::getBateria));
        }

        return candidatos.stream()
                .min(Comparator.comparingDouble(r -> {
                    Coordenada c = r.getCoordenada();
                    return c == null ? Double.MAX_VALUE : c.distanciaEuclidiana(destino);
                }));
    }

    @Override
    @Transactional
    public boolean realocarTarefa(Tarefa tarefa) {
        Robo anterior = tarefa.getRoboAtribuido();
        tarefa.desatribuir();
        tarefa.marcarRealocada();

        Optional<Robo> novo = escolherMelhorRobo(tarefa);
        if (novo.isPresent()) {
            Robo novoRobo = novo.get();
            novoRobo.setStatus(StatusRobo.EM_TAREFA);
            roboRepository.save(novoRobo);
            tarefa.atribuirA(novoRobo);
            tarefaRepository.save(tarefa);
            registrarAlertaRealocacao(tarefa, anterior, novoRobo);
            return true;
        }

        tarefaRepository.save(tarefa);
        registrarAlertaSemRoboDisponivel(tarefa, anterior);
        return false;
    }

    private void registrarAlertaRealocacao(Tarefa tarefa, Robo anterior, Robo novo) {
        Alerta a = new Alerta();
        a.setTipo(TipoAlerta.TAREFA_REALOCADA);
        a.setSeveridade(SeveridadeAlerta.AVISO);
        a.setMensagem("Tarefa %s realocada de %s para %s".formatted(
                tarefa.getCodigo(),
                anterior != null ? anterior.getCodigo() : "(sem robo)",
                novo.getCodigo()));
        a.setTarefa(tarefa);
        a.setRobo(novo);
        alertaRepository.save(a);
    }

    private void registrarAlertaSemRoboDisponivel(Tarefa tarefa, Robo anterior) {
        Alerta a = new Alerta();
        a.setTipo(TipoAlerta.TAREFA_SEM_ROBO_DISPONIVEL);
        a.setSeveridade(SeveridadeAlerta.CRITICO);
        a.setMensagem("Tarefa %s sem robo %s disponivel para realocar".formatted(
                tarefa.getCodigo(),
                tarefa.getTipoRoboRequerido()));
        a.setTarefa(tarefa);
        a.setRobo(anterior);
        alertaRepository.save(a);
    }
}
