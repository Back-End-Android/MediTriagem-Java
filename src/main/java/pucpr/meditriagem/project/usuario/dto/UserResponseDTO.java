package pucpr.meditriagem.project.usuario.dto;

import lombok.Getter;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;

@Getter
public class UserResponseDTO {

    private Long id;
    private String email;
    private Cargo cargo;

    public UserResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo();
    }
}