package br.com.swarmbuild.model;

import br.com.swarmbuild.model.enums.StatusTarefa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transicoes de estado da Tarefa")
class TarefaTest {

    @Test
    @DisplayName("atribuirA muda status para EM_EXECUCAO e registra inicio")
    void atribuir() {
        Tarefa t = new Tarefa();
        Robo robo = new RoboEscavadeira();

        t.atribuirA(robo);

        assertEquals(StatusTarefa.EM_EXECUCAO, t.getStatus());
        assertEquals(robo, t.getRoboAtribuido());
        assertNotNull(t.getIniciadaEm());
    }

    @Test
    @DisplayName("desatribuir volta para PENDENTE e remove o robo")
    void desatribuir() {
        Tarefa t = new Tarefa();
        t.atribuirA(new RoboEscavadeira());

        t.desatribuir();

        assertEquals(StatusTarefa.PENDENTE, t.getStatus());
        assertNull(t.getRoboAtribuido());
    }

    @Test
    @DisplayName("marcarRealocada incrementa o contador de realocacoes")
    void marcarRealocada() {
        Tarefa t = new Tarefa();
        t.setVezesRealocada(0);

        t.marcarRealocada();
        t.marcarRealocada();

        assertEquals(2, t.getVezesRealocada());
        assertEquals(StatusTarefa.REALOCADA, t.getStatus());
    }

    @Test
    @DisplayName("concluir registra data de conclusao")
    void concluir() {
        Tarefa t = new Tarefa();
        t.atribuirA(new RoboEscavadeira());

        t.concluir();

        assertEquals(StatusTarefa.CONCLUIDA, t.getStatus());
        assertNotNull(t.getConcluidaEm());
    }

    @Test
    @DisplayName("estaEmExecucao true para EM_EXECUCAO e REALOCADA")
    void estaEmExecucao() {
        Tarefa t = new Tarefa();
        t.setStatus(StatusTarefa.EM_EXECUCAO);
        assertTrue(t.estaEmExecucao());

        t.setStatus(StatusTarefa.REALOCADA);
        assertTrue(t.estaEmExecucao());

        t.setStatus(StatusTarefa.CONCLUIDA);
        assertFalse(t.estaEmExecucao());
    }
}
