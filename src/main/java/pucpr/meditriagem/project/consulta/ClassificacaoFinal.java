package pucpr.meditriagem.project.consulta;

// Classificação final de risco dada pelo médico (Protocolo de Manchester)
public enum ClassificacaoFinal {
    VERMELHO,      // Emergência (Atendimento Imediato)
    LARANJA,       // Muito Urgente
    AMARELO,       // Urgente
    VERDE,         // Pouco Urgente
    AZUL           // Não Urgente
}