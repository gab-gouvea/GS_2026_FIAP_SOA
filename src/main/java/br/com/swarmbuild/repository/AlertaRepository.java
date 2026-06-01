package br.com.swarmbuild.repository;

import br.com.swarmbuild.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByResolvidoOrderByCriadoEmDesc(Boolean resolvido);

    List<Alerta> findByRoboIdOrderByCriadoEmDesc(Long roboId);
}
