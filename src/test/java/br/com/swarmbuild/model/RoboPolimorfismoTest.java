package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoRobo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Heranca e polimorfismo de Robo")
class RoboPolimorfismoTest {

    @Test
    @DisplayName("cada subclasse retorna seu proprio tipo (polimorfismo)")
    void cadaSubclasseTemTipoProprio() {
        Robo escavadeira = new RoboEscavadeira();
        Robo transportador = new RoboTransportador();
        Robo montador = new RoboMontador();

        assertEquals(TipoRobo.ESCAVADEIRA, escavadeira.getTipo());
        assertEquals(TipoRobo.TRANSPORTADOR, transportador.getTipo());
        assertEquals(TipoRobo.MONTADOR, montador.getTipo());
    }

    @Test
    @DisplayName("descricaoCapacidade tem comportamento diferente por subclasse")
    void descricaoCapacidadePolimorfica() {
        RoboEscavadeira esc = new RoboEscavadeira();
        esc.setCapacidadeCargaKg(500.0);
        esc.setProfundidadeMaximaMetros(3.5);

        RoboMontador mont = new RoboMontador();
        mont.setPrecisaoMontagemMm(0.5);
        mont.setBracosManipuladores(4);

        assertTrue(esc.descricaoCapacidade().startsWith("Escavadeira"));
        assertTrue(mont.descricaoCapacidade().startsWith("Montador"));
        assertNotEquals(esc.descricaoCapacidade(), mont.descricaoCapacidade());
    }

    @Test
    @DisplayName("polimorfismo em colecao: chamada uniforme, resposta especifica")
    void polimorfismoEmColecao() {
        List<Robo> enxame = List.of(new RoboEscavadeira(), new RoboTransportador(), new RoboMontador());
        List<TipoRobo> tipos = enxame.stream().map(Robo::getTipo).toList();
        assertEquals(List.of(TipoRobo.ESCAVADEIRA, TipoRobo.TRANSPORTADOR, TipoRobo.MONTADOR), tipos);
    }

    @Test
    @DisplayName("estaDisponivel: false quando bateria muito baixa")
    void naoDisponivelComBateriaBaixa() {
        Robo robo = new RoboEscavadeira();
        robo.setStatus(StatusRobo.DISPONIVEL);
        robo.setBateria(5);
        assertFalse(robo.estaDisponivel());
    }

    @Test
    @DisplayName("estaDisponivel: false quando status nao e DISPONIVEL")
    void naoDisponivelQuandoEmTarefa() {
        Robo robo = new RoboEscavadeira();
        robo.setStatus(StatusRobo.EM_TAREFA);
        robo.setBateria(100);
        assertFalse(robo.estaDisponivel());
    }

    @Test
    @DisplayName("ehCompativelCom: so aceita tarefa do mesmo tipo")
    void compatibilidadePorTipo() {
        Robo escavadeira = new RoboEscavadeira();
        assertTrue(escavadeira.ehCompativelCom(TipoRobo.ESCAVADEIRA));
        assertFalse(escavadeira.ehCompativelCom(TipoRobo.MONTADOR));
    }
}
