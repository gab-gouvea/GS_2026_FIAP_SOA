package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.CriarRoboDTO;
import br.com.swarmbuild.model.*;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoRobo;
import br.com.swarmbuild.model.vo.Coordenada;
import br.com.swarmbuild.repository.AlertaRepository;
import br.com.swarmbuild.repository.RoboRepository;
import br.com.swarmbuild.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrquestradorEnxame - inteligencia do enxame")
class OrquestradorEnxameTest {

    @Mock RoboRepository roboRepository;
    @Mock TarefaRepository tarefaRepository;
    @Mock AlertaRepository alertaRepository;

    @InjectMocks OrquestradorEnxameImpl orquestrador;

    private Tarefa tarefa;

    @BeforeEach
    void setup() {
        tarefa = new Tarefa();
        tarefa.setCodigo("T-001");
        tarefa.setTipoRoboRequerido(TipoRobo.ESCAVADEIRA);
        tarefa.setLocalExecucao(new Coordenada(0.0, 0.0));
    }

    @Test
    @DisplayName("construirRoboAPartirDoDTO cria a subclasse correta conforme o tipo")
    void factoryPolimorfica() {
        CriarRoboDTO dtoEsc = new CriarRoboDTO("ESC-1", "n", "m", TipoRobo.ESCAVADEIRA,
                null, null, 500.0, 3.0, null, null, null, null);
        CriarRoboDTO dtoTrans = new CriarRoboDTO("TR-1", "n", "m", TipoRobo.TRANSPORTADOR,
                null, null, null, null, 200.0, 25.0, null, null);
        CriarRoboDTO dtoMont = new CriarRoboDTO("MO-1", "n", "m", TipoRobo.MONTADOR,
                null, null, null, null, null, null, 0.5, 4);

        assertInstanceOf(RoboEscavadeira.class, orquestrador.construirRoboAPartirDoDTO(dtoEsc));
        assertInstanceOf(RoboTransportador.class, orquestrador.construirRoboAPartirDoDTO(dtoTrans));
        assertInstanceOf(RoboMontador.class, orquestrador.construirRoboAPartirDoDTO(dtoMont));
    }

    @Test
    @DisplayName("escolherMelhorRobo escolhe o robo mais proximo do local da tarefa")
    void escolheMaisProximo() {
        Robo perto = escavadeiraDisponivel("ESC-PERTO", 0.1, 0.1);
        Robo longe = escavadeiraDisponivel("ESC-LONGE", 10.0, 10.0);
        when(roboRepository.findDisponiveis()).thenReturn(List.of(longe, perto));

        Optional<Robo> escolhido = orquestrador.escolherMelhorRobo(tarefa);

        assertTrue(escolhido.isPresent());
        assertEquals("ESC-PERTO", escolhido.get().getCodigo());
    }

    @Test
    @DisplayName("escolherMelhorRobo retorna vazio quando nao ha robo do tipo")
    void semRoboDisponivel() {
        when(roboRepository.findDisponiveis()).thenReturn(List.of());
        assertTrue(orquestrador.escolherMelhorRobo(tarefa).isEmpty());
    }

    @Test
    @DisplayName("escolherMelhorRobo ignora robo de tipo incompativel")
    void ignoraTipoIncompativel() {
        Robo montador = new RoboMontador();
        montador.setCodigo("MO-1");
        montador.setStatus(StatusRobo.DISPONIVEL);
        montador.setBateria(100);
        when(roboRepository.findDisponiveis()).thenReturn(List.of(montador));

        assertTrue(orquestrador.escolherMelhorRobo(tarefa).isEmpty());
    }

    @Test
    @DisplayName("realocarTarefa move a tarefa para outro robo e gera alerta")
    void realocaParaOutroRobo() {
        Robo falho = escavadeiraDisponivel("ESC-FALHO", 0.0, 0.0);
        falho.setStatus(StatusRobo.FALHA);
        tarefa.atribuirA(falho);

        Robo substituto = escavadeiraDisponivel("ESC-SUB", 0.0, 0.0);
        when(roboRepository.findDisponiveis()).thenReturn(List.of(substituto));

        boolean realocada = orquestrador.realocarTarefa(tarefa);

        assertTrue(realocada);
        assertEquals("ESC-SUB", tarefa.getRoboAtribuido().getCodigo());
        assertEquals(StatusRobo.EM_TAREFA, substituto.getStatus());
        verify(alertaRepository, atLeastOnce()).save(any(Alerta.class));
    }

    @Test
    @DisplayName("realocarTarefa libera o robo anterior saudavel (volta a DISPONIVEL)")
    void realocaLiberaRoboAnteriorSaudavel() {
        Robo anterior = escavadeiraDisponivel("ESC-ANT", 0.0, 0.0);
        tarefa.atribuirA(anterior);
        anterior.setStatus(StatusRobo.EM_TAREFA);

        Robo substituto = escavadeiraDisponivel("ESC-SUB", 0.0, 0.0);
        when(roboRepository.findDisponiveis()).thenReturn(List.of(substituto));

        orquestrador.realocarTarefa(tarefa);

        assertEquals(StatusRobo.DISPONIVEL, anterior.getStatus());
        assertEquals(StatusRobo.EM_TAREFA, substituto.getStatus());
        assertEquals("ESC-SUB", tarefa.getRoboAtribuido().getCodigo());
    }

    @Test
    @DisplayName("realocarTarefa NAO altera robo em FALHA (fluxo do monitor de falhas)")
    void realocaNaoLiberaRoboEmFalha() {
        Robo falho = escavadeiraDisponivel("ESC-FALHO", 0.0, 0.0);
        falho.setStatus(StatusRobo.FALHA);
        tarefa.atribuirA(falho);

        Robo substituto = escavadeiraDisponivel("ESC-SUB", 0.0, 0.0);
        when(roboRepository.findDisponiveis()).thenReturn(List.of(substituto));

        orquestrador.realocarTarefa(tarefa);

        assertEquals(StatusRobo.FALHA, falho.getStatus());
    }

    @Test
    @DisplayName("realocarTarefa gera alerta critico quando nao ha substituto")
    void realocaSemSubstituto() {
        Robo falho = escavadeiraDisponivel("ESC-FALHO", 0.0, 0.0);
        tarefa.atribuirA(falho);
        when(roboRepository.findDisponiveis()).thenReturn(List.of());

        boolean realocada = orquestrador.realocarTarefa(tarefa);

        assertFalse(realocada);
        verify(alertaRepository, atLeastOnce()).save(any(Alerta.class));
    }

    private Robo escavadeiraDisponivel(String codigo, double lat, double lon) {
        RoboEscavadeira r = new RoboEscavadeira();
        r.setCodigo(codigo);
        r.setStatus(StatusRobo.DISPONIVEL);
        r.setBateria(100);
        r.setCoordenada(new Coordenada(lat, lon));
        return r;
    }
}
