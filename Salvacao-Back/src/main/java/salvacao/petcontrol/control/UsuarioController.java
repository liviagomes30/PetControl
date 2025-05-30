package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.UsuarioModel;
import salvacao.petcontrol.dto.UsuarioCompletoDTO;
import salvacao.petcontrol.dto.LoginRequestDTO;
import salvacao.petcontrol.dto.PasswordUpdateDTO;
import salvacao.petcontrol.service.UsuarioService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        List<UsuarioCompletoDTO> usuarios = usuarioService.getAll();
        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum usuario encontrado");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        UsuarioCompletoDTO usuario = usuarioService.getId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
    }

    @GetMapping("/tipo/{idTipo}")
    public ResponseEntity<Object> getByTipo(@PathVariable Integer idTipo) {
        List<UsuarioCompletoDTO> usuarios = usuarioService.getUsuariosByTipo(idTipo);
        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum usuario encontrado para o tipo especificado");
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String termo) {
        List<UsuarioCompletoDTO> usuarios = usuarioService.getByName(termo);
        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum usuario encontrado com o termo: " + termo);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> gravar(@RequestBody UsuarioCompletoDTO usuario) {
        try {
            UsuarioModel novoUsuario = usuarioService.gravar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody UsuarioCompletoDTO usuario) {
        try {
            boolean atualizado = usuarioService.alterar(id, usuario);
            if (atualizado) {
                return ResponseEntity.ok("Usuario atualizado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar o usuario");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagarUsuario(@PathVariable Integer id) {
        try {
            ResultadoOperacao resultado = usuarioService.apagarUsuario(id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Object> reativarUsuario(@PathVariable Integer id) {
        try {
            boolean reativado = usuarioService.reativarUsuario(id);
            if (reativado) {
                return ResponseEntity.ok("Usuario reativado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível reativar o usuario");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/listar-filtrados")
    public ResponseEntity<Object> listarUsuariosFiltrados(@RequestParam(value = "filtro", required = false, defaultValue = "") String filtro) {
        try {
            List<UsuarioCompletoDTO> usuarios = usuarioService.listarUsuariosFiltrados(filtro);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao filtrar usuários: " + e.getMessage());
        }
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getNome(@PathVariable String filtro) {
        List<UsuarioCompletoDTO> usuarioList = usuarioService.getByName(filtro);
        if (!usuarioList.isEmpty())
            return ResponseEntity.ok(usuarioList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Usuario encontrado");
    }

    @GetMapping("/fabricante/{filtro}")
    public ResponseEntity<Object> getByFabricante(@PathVariable String filtro) {
        List<UsuarioCompletoDTO> usuarioList = usuarioService.getByFabricante(filtro);
        if (!usuarioList.isEmpty())
            return ResponseEntity.ok(usuarioList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Usuario encontrado");
    }

    @GetMapping("/tipoDescricao/{filtro}")
    public ResponseEntity<Object> getByTipoDescricao(@PathVariable String filtro) {
        List<UsuarioCompletoDTO> usuarioList = usuarioService.getByTipoDescricao(filtro);
        if (!usuarioList.isEmpty())
            return ResponseEntity.ok(usuarioList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Usuario encontrado");
    }

    // Authentication endpoints
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            UsuarioCompletoDTO usuario = usuarioService.authenticate(loginRequest.getLogin(), loginRequest.getSenha());
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<Object> findByLogin(@PathVariable String login) {
        try {
            UsuarioCompletoDTO usuario = usuarioService.findByLogin(login);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Object> updatePassword(@PathVariable Integer id,
            @RequestBody PasswordUpdateDTO passwordUpdate) {
        try {
            boolean atualizado = usuarioService.updatePassword(id, passwordUpdate.getNovaSenha());
            if (atualizado) {
                return ResponseEntity.ok("Senha atualizada com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar a senha");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-login/{login}")
    public ResponseEntity<Object> checkLoginExists(@PathVariable String login) {
        boolean exists = usuarioService.loginExists(login);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}