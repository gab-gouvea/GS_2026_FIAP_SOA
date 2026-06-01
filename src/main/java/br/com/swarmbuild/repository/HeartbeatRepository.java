package br.com.swarmbuild.repository;

import br.com.swarmbuild.model.Heartbeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartbeatRepository extends JpaRepository<Heartbeat, Long> {

    List<Heartbeat> findByRoboIdOrderByTimestampDesc(Long roboId);
}
