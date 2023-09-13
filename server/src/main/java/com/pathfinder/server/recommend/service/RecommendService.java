package com.pathfinder.server.recommend.service;

import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.member.service.MemberService;
import com.pathfinder.server.recommend.entity.Recommend;
import com.pathfinder.server.recommend.repository.RecommendRepository;
import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.diary.service.DiaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final MemberService memberService;
    private final DiaryService diaryService;

    public RecommendService(RecommendRepository recommendRepository, MemberService memberService, DiaryService diaryService) {
        this.recommendRepository = recommendRepository;
        this.memberService = memberService;
        this.diaryService = diaryService;
    }

    public void toggleRecommend(Long memberId, Long diaryId) {
        Optional<Recommend> optionalRecommend = recommendRepository.findByMemberMemberIdAndDiaryDiaryId(memberId, diaryId);

        if (optionalRecommend.isPresent()) {
            optionalRecommend.get().getDiary().setRecommend(false);
            recommendRepository.delete(optionalRecommend.get());

        } else {
            Member member = memberService.findVerifiedMember(memberId);
            Diary diary = diaryService.findVerifiedDiary(diaryId);
            Recommend recommend = new Recommend();
            recommend.setMember(member);
            recommend.setDiary(diary);
            diary.setRecommend(false);
            recommendRepository.save(recommend);
        }
    }
}
