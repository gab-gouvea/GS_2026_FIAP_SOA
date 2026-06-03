package br.com.swarmbuild.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RoboNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRoboNaoEncontrado(RoboNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.simples(404, "Robo nao encontrado", ex.getMessage()));
    }

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarTarefaNaoEncontrada(TarefaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.simples(404, "Tarefa nao encontrada", ex.getMessage()));
    }

    @ExceptionHandler(AlertaNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarAlertaNaoEncontrado(AlertaNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.simples(404, "Alerta nao encontrado", ex.getMessage()));
    }

    @ExceptionHandler(CodigoDuplicadoException.class)
    public ResponseEntity<ErroResponse> tratarCodigoDuplicado(CodigoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResponse.simples(409, "Codigo duplicado", ex.getMessage()));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErroResponse.simples(400, "Regra de negocio violada", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErroResponse.comCampos(400, "Dados invalidos", "Verifique os campos enviados", campos));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErroResponse.simples(400, "Argumento invalido", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarViolacaoDeIntegridade(DataIntegrityViolationException ex) {
        log.warn("Violacao de integridade referencial bloqueada", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResponse.simples(409, "Operacao bloqueada",
                        "Operacao viola a integridade dos dados (registro referenciado por outros recursos)"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex) {
        // Sistemas criticos nao podem vazar detalhes internos (SQL, stack trace) ao cliente:
        // o erro real fica no log do servidor e o cliente recebe uma mensagem generica.
        log.error("Erro interno nao tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.simples(500, "Erro interno",
                        "Ocorreu um erro inesperado. A equipe foi notificada."));
    }
}
