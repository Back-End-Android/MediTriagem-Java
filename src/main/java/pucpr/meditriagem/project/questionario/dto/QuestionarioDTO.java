package pucpr.meditriagem.project.questionario.dto;
import pucpr.meditriagem.project.questionario.LocalCorpo;


public record QuestionarioDTO(
    // Questionário de sintomas
    boolean febre,
    boolean tontura,
    boolean fraqueza,
    boolean faltaDeAr,
    boolean diarreia,
    boolean nausea,
    boolean vomito,
    boolean dor,
    boolean tosse,
    boolean sangramento,
    boolean alteracaoPressao,
    boolean fratura,

    // Campos descritivos extras
    LocalCorpo localDor,
    LocalCorpo localFratura,
    LocalCorpo localSangramento,
    String tipoTosse
){
}
