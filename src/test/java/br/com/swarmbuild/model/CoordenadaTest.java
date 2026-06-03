package br.com.swarmbuild.model;

import br.com.swarmbuild.model.vo.Coordenada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VO Coordenada")
class CoordenadaTest {

    @Test
    @DisplayName("calcula distancia euclidiana entre dois pontos")
    void calculaDistancia() {
        Coordenada a = new Coordenada(0.0, 0.0);
        Coordenada b = new Coordenada(3.0, 4.0);
        assertEquals(5.0, a.distanciaEuclidiana(b), 0.0001);
    }

    @Test
    @DisplayName("distancia para o mesmo ponto e zero")
    void distanciaZero() {
        Coordenada a = new Coordenada(-3.05, 23.42);
        assertEquals(0.0, a.distanciaEuclidiana(a), 0.0001);
    }

    @Test
    @DisplayName("distancia com coordenada nula retorna MAX_VALUE")
    void distanciaComNulo() {
        Coordenada a = new Coordenada(1.0, 1.0);
        assertEquals(Double.MAX_VALUE, a.distanciaEuclidiana(null));
    }

    @Test
    @DisplayName("rejeita latitude fora do intervalo [-90, 90]")
    void rejeitaLatitudeInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(91.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(-91.0, 0.0));
    }

    @Test
    @DisplayName("rejeita longitude fora do intervalo [-180, 180]")
    void rejeitaLongitudeInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(0.0, 181.0));
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(0.0, -181.0));
    }
}
