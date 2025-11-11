package pucpr.meditriagem.project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component // Diz ao Spring para "construir" este componente
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private MessageSource messageSource; // Injeta o "Tradutor"

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Pega a mensagem do seu 'messages.properties'
        String mensagem = messageSource.getMessage("error.unauthorized", null, LocaleContextHolder.getLocale());

        // Define a resposta como 401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        // Cria o JSON de erro manualmente
        Map<String, Object> body = Map.of(
                "status", HttpStatus.UNAUTHORIZED.value(),
                "mensagem", mensagem, // "ERR-004: Acesso não autorizado..."
                "timestamp", LocalDateTime.now().toString()
        );

        // Escreve o JSON na resposta
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}