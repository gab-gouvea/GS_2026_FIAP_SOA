package br.com.swarmbuild.repository;

import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.enums.StatusRobo;
import br.com.swarmbuild.model.enums.TipoRobo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoboRepository extends JpaRepository<Robo, Long> {

    Optional<Robo> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Robo> findByStatus(StatusRobo status);

    @Query("SELECT r FROM Robo r WHERE r.status = br.com.swarmbuild.model.enums.StatusRobo.DISPONIVEL")
    List<Robo> findDisponiveis();

    @Query("SELECT r FROM Robo r WHERE r.ultimoHeartbeat IS NOT NULL AND r.ultimoHeartbeat < :limite AND r.status <> br.com.swarmbuild.model.enums.StatusRobo.FALHA AND r.status <> br.com.swarmbuild.model.enums.StatusRobo.MANUTENCAO")
    List<Robo> findOffline(LocalDateTime limite);

    default List<Robo> findDisponiveisPorTipo(TipoRobo tipo) {
        return findDisponiveis().stream()
                .filter(r -> r.getTipo() == tipo)
                .toList();
    }
}
