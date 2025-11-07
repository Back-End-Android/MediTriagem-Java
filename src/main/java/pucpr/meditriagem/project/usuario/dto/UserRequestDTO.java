package pucpr.meditriagem.project.usuario.dto;

import lombok.Getter;
import pucpr.meditriagem.project.usuario.Cargo;

@Getter
public class UserRequestDTO {
    private String email;
    private String senha;
    private Cargo cargo;
}