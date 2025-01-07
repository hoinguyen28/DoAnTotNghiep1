package vn.ptit.webTranh_backend.controller;
import vn.ptit.webTranh_backend.entity.User;
import vn.ptit.webTranh_backend.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artist")
public class ArtistController {

    private final UserService userService;

    @Autowired
    public ArtistController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArtistById(@PathVariable int id) {
        try {
            User user = userService.getUserById(Math.toIntExact(id));
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("Artist not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("An error occurred: " + ex.getMessage());
        }
    }
}
