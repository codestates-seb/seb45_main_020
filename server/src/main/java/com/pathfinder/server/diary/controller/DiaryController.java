package com.pathfinder.server.diary.controller;

import com.pathfinder.server.auth.utils.SecurityUtil;
import com.pathfinder.server.diary.dto.DiaryDto;
import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.diary.mapper.DiaryMapper;
import com.pathfinder.server.diary.service.DiaryService;
import com.pathfinder.server.dto.MultiResponseDto;
import com.pathfinder.server.dto.SingleResponseDto;
import com.pathfinder.server.recommend.entity.Recommend;
import com.pathfinder.server.recommend.repository.RecommendRepository;
import com.pathfinder.server.scrap.entity.Scrap;
import com.pathfinder.server.scrap.repository.ScrapRepository;
import com.pathfinder.server.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diary")
public class DiaryController {
    private final static String DIARY_DEFAULT_URL = "/diary";
    private DiaryService diaryService;
    private DiaryMapper mapper;
    private final RecommendRepository recommendRepository;
    private final ScrapRepository scrapRepository;

    public DiaryController(DiaryService diaryService, DiaryMapper mapper, RecommendRepository recommendRepository, ScrapRepository scrapRepository) {
        this.diaryService = diaryService;
        this.mapper = mapper;
        this.recommendRepository = recommendRepository;
        this.scrapRepository = scrapRepository;
    }

    @PostMapping("/registration") // 게시글 생성
    public ResponseEntity postDiary(@RequestBody DiaryDto.Post diaryPostDto) {
        Diary diary = diaryService.createDiary(mapper.diaryPostDtoToDiary(diaryPostDto));

        URI location = UriCreator.createUri(DIARY_DEFAULT_URL, diary.getDiaryId());

        return ResponseEntity.created(location).build();
    }


    @GetMapping("/{diary-id}") //게시글 자세히 보기
    public ResponseEntity getDiary(@PathVariable("diary-id") @Positive Long diaryId) {
        Diary diary = diaryService.getDiary(diaryId);
        DiaryDto.Response response = mapper.DiaryToDiaryResponseDto(diary);
        Optional<Recommend> optionalRecommend = recommendRepository.findByMemberMemberIdAndDiaryDiaryId(SecurityUtil.getCurrentId(), diaryId);
        response.setRecommend(optionalRecommend.isPresent());
        Optional<Scrap> optionalScrap = scrapRepository.findByMemberMemberIdAndDiaryDiaryId(SecurityUtil.getCurrentId(),diaryId);
        response.setScrap(optionalScrap.isPresent());
        return new ResponseEntity<>(
                new SingleResponseDto<>(response),
                HttpStatus.OK);
    }

    @GetMapping // 전체 게시글 조회
    public ResponseEntity getDiaries(@RequestParam int page) {
        Page<Diary> pageDiaries = diaryService.getDiaries(page - 1);

        List<Diary> diaries = pageDiaries.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.DiariesToDiaryResponseDtos(diaries),
                        pageDiaries),
                HttpStatus.OK);
    }
    @GetMapping("/area/{area1}") // area1을 갖는 게시글 조회
    public ResponseEntity getDiariesByRegion(@PathVariable("area1") String area1,
                                             @RequestParam int page) {
        Page<Diary> pageDiaries = diaryService.getDiariesByRegion(area1, page);

        List<Diary> diaries = pageDiaries.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.DiariesToDiaryResponseDtos(diaries),
                        pageDiaries),
                HttpStatus.OK);
    }

    @GetMapping("/member/{member-id}") // member가 작성한 게시글 조회
    public ResponseEntity getDiariesByRegion(@PathVariable("member-id") Long memberId,
                                             @RequestParam int page) {
        Page<Diary> pageDiaries = diaryService.getDiariesByMember(memberId, page);

        List<Diary> diaries = pageDiaries.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.DiariesToDiaryResponseDtos(diaries),
                        pageDiaries),
                HttpStatus.OK);
    }

    @GetMapping("/recommend") // 추천순으로 상위 3개 게시글 리스트 조회
    public ResponseEntity getDiariesByRecommendedCount() {
        Page<Diary> pageDiaries = diaryService.getTop3DiariesByRecommendedCount();

        List<Diary> top3Diaries = pageDiaries.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.DiariesToDiaryResponseDtos(top3Diaries),
                        pageDiaries),
                HttpStatus.OK);
    }

    @PatchMapping("/edit/{diary-id}")
    public ResponseEntity patchDiary(@PathVariable("diary-id") long diaryId,
                                      @RequestBody DiaryDto.Patch diaryPatchDto) {
        diaryPatchDto.setDiaryId(diaryId);
        Diary diary = diaryService.updateDiary(mapper.diaryPatchDtoToDiary(diaryPatchDto));

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.DiaryToDiaryResponseDto(diary)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{diary-id}")
    public ResponseEntity deleteDiary(@PathVariable("diary-id") Long diaryId){
        diaryService.deleteDiary(diaryId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
