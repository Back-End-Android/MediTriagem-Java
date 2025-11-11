package pucpr.meditriagem.project.exceptions;

// Esta exceção vai carregar o CÓDIGO do erro (ex: "paciente.not_found")
public class ResourceNotFoundException extends RuntimeException {

    // Construtor que aceita o CÓDIGO da mensagem
    public ResourceNotFoundException(String messageCode) {
        super(messageCode);
    }
}