package pucpr.meditriagem.project.exceptions;

// Para erros de permissão (ex: "Apenas enfermeiros...")
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String messageCode) {
        super(messageCode);
    }
}