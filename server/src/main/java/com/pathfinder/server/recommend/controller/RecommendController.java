package com.pathfinder.server.recommend.controller;

import com.pathfinder.server.recommend.service.RecommendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping()
    public ResponseEntity memberSignup(@RequestParam @Positive Long memberId,
                                       @RequestParam @Positive Long threadId) {
        recommendService.toggleRecommend(memberId, threadId);

        return new ResponseEntity(HttpStatus.OK);
    }
}