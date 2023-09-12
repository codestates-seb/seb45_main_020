package com.pathfinder.server.diary.service;

import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.diary.repository.DiaryRepository;
import com.pathfinder.server.global.exception.diaryexception.DiaryNotFoundException;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.member.service.MemberService;
import com.pathfinder.server.reward.service.RewardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final RewardService rewardService;

    public DiaryService(DiaryRepository diaryRepository, MemberService memberService, RewardService rewardService) {
        this.diaryRepository = diaryRepository;
        this.memberService = memberService;
        this.rewardService = rewardService;
    }

    public Diary createDiary(Diary diary) {
        verifyDiaryGetMemberName(diary);

        return diaryRepository.save(diary);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Diary updateDiary(Diary diary){
        Diary findDiary = findVerifiedDiary(diary.getDiaryId());
        Optional.ofNullable(diary.getTitle())
                .ifPresent(title -> findDiary.setTitle(title));
        Optional.ofNullable(diary.getContent())
                .ifPresent(content -> findDiary.setContent(content));
        Optional.ofNullable(diary.getArea1())
                .ifPresent(area1 -> findDiary.setArea1(area1));
        Optional.ofNullable(diary.getArea2())
                .ifPresent(area2 -> findDiary.setArea2(area2));
        return diaryRepository.save(findDiary);
    }

    @Transactional(readOnly = true)
    public Diary getDiary(Long diaryId){
        Diary findDiary = findVerifiedDiary(diaryId);
        findDiary.setViews(findDiary.getViews() + 1); // 조회수 증가
        findDiary.setRecommendedCount(findDiary.getRecommends().stream().count());

        return diaryRepository.save(findDiary);
    }

    public Page<Diary> getDiaries(int page){
        return diaryRepository.findAll(PageRequest.of(page, 10, Sort.by("diaryId").descending()));
    }

    public Page<Diary> getDiariesByRegion(String area1, int page){
        return diaryRepository.findByArea1(area1, PageRequest.of(page - 1,10, Sort.by("diaryId").descending()));
    }

    public Page<Diary> getDiariesByMember(Long memberId, int page){
        return diaryRepository.findByMemberMemberId(memberId, PageRequest.of(page - 1,10, Sort.by("diaryId").descending()));
    }

    public Page<Diary> getTop3DiariesByRecommendedCount() {
        return diaryRepository.findByTop3ByOrderedByRecommendedCount(PageRequest.of(0,3,Sort.by("recommendedCount").descending()));
    }

    public void deleteDiary(Long diaryId) {
        Diary findDiary = findVerifiedDiary(diaryId);

        diaryRepository.delete(findDiary);
    }

    @Transactional(readOnly = true)
    public Diary findVerifiedDiary(Long diaryId) {
        Optional<Diary> optionalQuestion = diaryRepository.findById(diaryId);
        Diary findDiary = optionalQuestion.orElseThrow(()-> new DiaryNotFoundException());
        return findDiary;
    }
    private void verifyDiaryGetMemberName(Diary diary){
        Member findUser = memberService.findMember(diary.getMember().getMemberId());
        diary.setName(findUser.getName());
        findUser.setDiaryCount(findUser.getDiaryCount() + 1);
        rewardService.unlockRewards(findUser,findUser.getRewards());
    }
}
