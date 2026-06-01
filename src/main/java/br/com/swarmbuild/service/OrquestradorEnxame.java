package br.com.swarmbuild.service;

import br.com.swarmbuild.dto.CriarRoboDTO;
import br.com.swarmbuild.model.Robo;
import br.com.swarmbuild.model.Tarefa;

import java.util.Optional;

public interface OrquestradorEnxame {

    Robo construirRoboAPartirDoDTO(CriarRoboDTO dto);

    Optional<Robo> escolherMelhorRobo(Tarefa tarefa);

    boolean realocarTarefa(Tarefa tarefa);
}
