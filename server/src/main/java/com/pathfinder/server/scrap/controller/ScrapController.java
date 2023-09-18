package com.pathfinder.server.scrap.controller;

import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.dto.MultiResponseDto;
import com.pathfinder.server.scrap.dto.ScrapDto;
import com.pathfinder.server.scrap.entity.Scrap;
import com.pathfinder.server.scrap.mapper.ScrapMapper;
import com.pathfinder.server.scrap.service.ScrapService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/scrap")
@Validated
public class ScrapController {

    private final ScrapService scrapService;
    private final ScrapMapper mapper;

    public ScrapController(ScrapService scrapService, ScrapMapper mapper) {
        this.scrapService = scrapService;
        this.mapper = mapper;
    }

    @PostMapping //게시글 스크랩 하기
    public ResponseEntity postScrap(@RequestBody ScrapDto.Post scrapPostDto){
        scrapService.createScrap(mapper.scarpPostDtoToScrap(scrapPostDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/{member-id}")
    public ResponseEntity getScraps(@PathVariable("member-id") @Positive Long memberId,
                                    @RequestParam int page) {
        Page<Scrap> pageScraps = scrapService.getScrapsByMember(memberId, page-1);

        List<Scrap> scraps = pageScraps.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.scrapsToScrapResponseDtos(scraps),
                        pageScraps),
                HttpStatus.OK);
    }
}
