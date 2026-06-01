package br.com.swarmbuild.exception;

public class CodigoDuplicadoException extends RuntimeException {

    public CodigoDuplicadoException(String codigo) {
        super("Codigo ja em uso: " + codigo);
    }
}
