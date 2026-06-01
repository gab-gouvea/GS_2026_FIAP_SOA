package br.com.swarmbuild.repository;

import br.com.swarmbuild.model.Tarefa;
import br.com.swarmbuild.model.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    Optional<Tarefa> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Tarefa> findByStatus(StatusTarefa status);

    @Query("SELECT t FROM Tarefa t WHERE t.roboAtribuido.id = :roboId AND (t.status = br.com.swarmbuild.model.enums.StatusTarefa.EM_EXECUCAO OR t.status = br.com.swarmbuild.model.enums.StatusTarefa.REALOCADA)")
    List<Tarefa> findEmExecucaoPorRobo(Long roboId);
}
