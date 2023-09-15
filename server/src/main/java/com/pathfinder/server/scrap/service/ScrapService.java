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
import org.springframework.stereotype.Service;

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

    public Scrap createScrap(Scrap scrap) {
        Diary findDiary = diaryService.findVerifiedDiary(scrap.getDiary().getDiaryId());
        memberService.findMember(scrap.getMember().getMemberId());

        if(!scrapRepository.findByMemberMemberIdAndDiaryDiaryId(scrap.getMember().getMemberId(),scrap.getDiary().getDiaryId()).isPresent()) {
            findDiary.setScrapCount(findDiary.getScrapCount() + 1);
            return scrapRepository.save(scrap);
        }
        else {
            throw new AlreadyScrapException();
        }
    }

    public Page<Scrap> getScrapsByMember(Long memberId, int page){ //멤버의 스크랩 게시글 조회

        return scrapRepository.findByMemberMemberId(memberId, PageRequest.of(page,10, Sort.by("scrapId").descending()));
    }

}