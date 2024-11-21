package vn.ptit.webTranh_backend.service.Art;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
public interface ArtService {
    public ResponseEntity<?> save(JsonNode artJson);
    public ResponseEntity<?> update(JsonNode artJson);
    public long getTotalArt();
}
