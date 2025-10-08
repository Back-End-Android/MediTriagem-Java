package pucpr.meditriagem.project.dto;
import pucpr.meditriagem.project.triagem.LocalCorpo;


public record QuestionarioDTO(
    // Questionário de sintomas
    boolean febre,
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
