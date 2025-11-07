package pucpr.meditriagem.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.dto.TriagemDTO;
import pucpr.meditriagem.project.triagem.Triagem;
import pucpr.meditriagem.project.triagem.TriagemService;

import java.util.List;

@RestController
@RequestMapping("/api/triagem")
public class TriagemController {

    @Autowired
    private TriagemService triagemService;

    @PostMapping("/criar")
    public ResponseEntity<Triagem> criarTriagem(@RequestBody TriagemDTO triagemDTO) {
        Triagem novaTriagem = triagemService.criarTriagem(triagemDTO.getUsuario().getId(), triagemDTO.getQuestionario());
        return ResponseEntity.ok(novaTriagem);
    }

    @GetMapping("/get_all")
    public List<Triagem> listarTriagens(){
        return triagemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Triagem> buscarPorId(@PathVariable Long id) {
        Triagem triagem = triagemService.findById(id);
        return ResponseEntity.ok(triagem);
    }


//    @PutMapping("/update/{id}")
    //@Operation(summary = "Atualiza a triagem pelo ID")
//    public Boolean atualizar(){
//        return triagemService.update();
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        triagemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
