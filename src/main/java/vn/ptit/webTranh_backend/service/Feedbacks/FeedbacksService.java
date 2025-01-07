package vn.ptit.webTranh_backend.service.Feedbacks;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import vn.ptit.webTranh_backend.entity.Feedbacks;

public interface FeedbacksService {
    public ResponseEntity<?> save(JsonNode feedbacksJson);
}
