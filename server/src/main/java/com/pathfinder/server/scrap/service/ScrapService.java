package com.pathfinder.server.scrap.service;


import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.diary.service.DiaryService;
import com.pathfinder.server.global.exception.scrapexception.AlreadyScrapException;
import com.pathfinder.server.member.service.MemberService;
import com.pathfinder.server.reward.entity.Reward;
import com.pathfinder.server.scrap.entity.Scrap;
import com.pathfinder.server.scrap.repository.ScrapRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final DiaryService diaryService;
    private final MemberService memberService;

    public ScrapService(ScrapRepository scrapRepository, DiaryService diaryService, MemberService memberService) {
        this.scrapRepository = scrapRepository;
        this.diaryService = diaryService;
        this.memberService = memberService;
    }

    public void createScrap(Scrap scrap) {
        Diary findDiary = diaryService.findVerifiedDiary(scrap.getDiary().getDiaryId());
        memberService.findMember(scrap.getMember().getMemberId());
        Optional<Scrap> optionalScrap = scrapRepository.findByMemberMemberIdAndDiaryDiaryId(scrap.getMember().getMemberId(),scrap.getDiary().getDiaryId());
        if(!optionalScrap.isPresent()) {
            findDiary.setScrapCount(findDiary.getScrapCount() + 1);
            scrapRepository.save(scrap);
        }
        else {
            findDiary.setScrapCount(findDiary.getScrapCount() - 1);
            scrapRepository.delete(optionalScrap.get());
        }
    }

    public Page<Scrap> getScrapsByMember(Long memberId, int page){ //멤버의 스크랩 게시글 조회

        return scrapRepository.findByMemberMemberId(memberId, PageRequest.of(page,10, Sort.by("scrapId").descending()));
    }

}
