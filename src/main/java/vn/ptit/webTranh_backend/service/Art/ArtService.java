package vn.ptit.webTranh_backend.service.Art;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import vn.ptit.webTranh_backend.entity.Art;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ArtService {
    public ResponseEntity<?> save(JsonNode artJson);
    public ResponseEntity<?> update(JsonNode artJson);
    void updateArtReviewStatus(int idArt, String reviewStatus);
    public long getTotalArt();

}
