package pucpr.meditriagem.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import pucpr.meditriagem.project.especialidade.Especialidade;
import pucpr.meditriagem.project.especialidade.EspecialidadeRepository;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Override
    public void run(String... args) throws Exception {

        // CRIAR ADMIN (adm inicial do sistema)

        String adminEmail = "admin@meditriagem.com";
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            var senhaCriptografada = passwordEncoder.encode("admin123");
            var adminUsuario = new Usuario(
                    null,
                    adminEmail,
                    senhaCriptografada,
                    Cargo.ADMIN
            );
            usuarioRepository.save(adminUsuario);

            System.out.println("*************************************************");
            System.out.println("ADMIN CRIADO COM SUCESSO");
            System.out.println("Email: " + adminEmail);
            System.out.println("Senha padrao: admin123");
            System.out.println("*************************************************");
        }


        // ESPECIALIDADES

        System.out.println("Verificando/Criando especialidades básicas...");

        //  lista especialidades
        List<String> especialidadesNomes = List.of(
                "Cardiologia",
                "Ortopedia",
                "Clínica Geral",
                "Dermatologia",
                "Ginecologia",
                "Pediatria",
                "Neurologia",
                "Oftalmologia",
                "Urologia",
                "Psiquiatria"
        );

        for (String nome : especialidadesNomes) {
            if (especialidadeRepository.findByNome(nome).isEmpty()) {

                Especialidade novaEspecialidade = new Especialidade(nome);
                especialidadeRepository.save(novaEspecialidade);
            }
        }

        System.out.println(especialidadesNomes.size() + " especialidades criadas");
        System.out.println("*************************************************");
    }
}