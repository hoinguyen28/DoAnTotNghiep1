package vn.ptit.webTranh_backend.controller;

import vn.ptit.webTranh_backend.service.Art.ArtService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/art")
public class ArtController {
    @Autowired
    private ArtService artService;

    @PostMapping(path = "/add-art")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonData) {
        try {
            return artService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/update-art")
    public ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            return artService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/get-total")
    public long getTotal() {
        return artService.getTotalArt();
    }
}
