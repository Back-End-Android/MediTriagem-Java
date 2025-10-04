package pucpr.meditriagem.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    @NotNull(message = "{error.nome.notnull}")
    @NotBlank(message = "{error.nome.notblank}")
    private String nome;

    @Email(message = "{error.email.invalid}")
    @NotBlank(message = "{error.email.notblank}")
    private String email;
    private String senha;
    private String cargo;
}
