package pucpr.meditriagem.project.exceptions;

// Para regras de negócio (ex: "Temperatura deve ser medida...")
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String messageCode) {
        super(messageCode);
    }
}