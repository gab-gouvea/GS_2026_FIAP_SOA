package br.com.swarmbuild.exception;

public class RoboNaoEncontradoException extends RuntimeException {

    public RoboNaoEncontradoException(Long id) {
        super("Robo nao encontrado: id=" + id);
    }

    public RoboNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
