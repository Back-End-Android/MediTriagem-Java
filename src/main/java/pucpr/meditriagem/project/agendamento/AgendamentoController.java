package pucpr.meditriagem.project.agendamento;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.agendamento.AgendamentoService;
import pucpr.meditriagem.project.agendamento.dto.AgendamentoDTO;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoDTO criar(@RequestBody @Valid AgendamentoDTO dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<AgendamentoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AgendamentoDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AgendamentoDTO atualizar(@PathVariable Long id, @RequestBody @Valid AgendamentoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
