package vn.ptit.webTranh_backend.controller;
import vn.ptit.webTranh_backend.entity.User;
import vn.ptit.webTranh_backend.service.Art.ArtRecommendationService;
import vn.ptit.webTranh_backend.dao.ArtRepository;
import vn.ptit.webTranh_backend.entity.Art;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendations")
public class ArtRecommendationController {

    @Autowired
    private ArtRecommendationService artRecommendationService;
    @Autowired
    private ArtRepository artRepository ;

    // API để lấy 3 bức tranh gợi ý cho người dùng
    @GetMapping("/top-similar/{userId}")
    public List<Art> getUserInteractedArts(@PathVariable int userId) {
        return artRecommendationService.getTopSimilarArts(userId);
    }
    @GetMapping("/top_similar/{userId}")
    public List<Art> getTopSimilarArt(@PathVariable int userId) {
        List<Art> allArts = artRepository.findAll();
        Collections.shuffle(allArts);
        return allArts.stream().limit(4).collect(Collectors.toList());
    }
}
