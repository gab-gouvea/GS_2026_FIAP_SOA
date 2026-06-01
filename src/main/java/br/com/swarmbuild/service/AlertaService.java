package br.com.swarmbuild.service;

import br.com.swarmbuild.exception.AlertaNaoEncontradoException;
import br.com.swarmbuild.model.Alerta;
import br.com.swarmbuild.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<Alerta> listar(Boolean resolvido) {
        if (resolvido == null) {
            return alertaRepository.findAll();
        }
        return alertaRepository.findByResolvidoOrderByCriadoEmDesc(resolvido);
    }

    public Alerta buscar(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new AlertaNaoEncontradoException(id));
    }

    @Transactional
    public Alerta resolver(Long id) {
        Alerta a = buscar(id);
        a.resolver();
        return alertaRepository.save(a);
    }
}
