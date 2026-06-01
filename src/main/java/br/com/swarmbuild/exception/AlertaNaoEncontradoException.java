package br.com.swarmbuild.exception;

public class AlertaNaoEncontradoException extends RuntimeException {

    public AlertaNaoEncontradoException(Long id) {
        super("Alerta nao encontrado: id=" + id);
    }
}
