package pucpr.meditriagem.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // 1. Importar o erro genérico 403
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Importe suas novas exceções
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.exceptions.ForbiddenOperationException;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    // Função "helper" para traduzir o código
    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    // Função "helper" para criar o JSON de resposta
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String messageCode) {
        String mensagem = getMessage(messageCode);

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "mensagem", mensagem,
                "timestamp", LocalDateTime.now()
        );
        return new ResponseEntity<>(body, status);
    }

    // --- Mapeamento das Nossas Exceções Customizadas (Correto) ---

    // Erro 404 (Não Encontrado) - USA CÓDIGOS ESPECÍFICOS (ex: "paciente.not_found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Erro 403 (Proibido) - USA CÓDIGOS ESPECÍFICOS (ex: "paciente.unauthorized")
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenOperationException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Erro 400 (Regra de Negócio) - USA CÓDIGOS ESPECÍFICOS (ex: "paciente.cpf.duplicado")
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Object> handleBusinessRule(BusinessRuleException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Mapeamento dos Erros Gerais (O seu pedido) ---

    // 2. Erro 400 genérico (@Valid) - USA O CÓDIGO "error.validation"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Pega a mensagem de validação (ex: "O nome não pode estar em branco.")
        String erroEspecifico = ex.getBindingResult().getFieldError().getDefaultMessage();
        String mensagemBase = getMessage("error.validation"); // "ERR-003: Dados de entrada inválidos"

        Map<String, Object> body = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "mensagem", mensagemBase + ". Detalhe: " + erroEspecifico,
                "timestamp", LocalDateTime.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        ex.printStackTrace(); // Bom para logar
        return buildErrorResponse(HttpStatus.FORBIDDEN, "error.forbidden");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Loga o erro real no console (importante!)
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "error.generic");
    }
}