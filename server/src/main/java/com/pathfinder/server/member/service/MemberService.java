package com.pathfinder.server.member.service;

import com.pathfinder.server.global.exception.memberexception.MemberEmailExistException;
import com.pathfinder.server.global.exception.memberexception.MemberNameExistException;
import com.pathfinder.server.global.exception.memberexception.MemberNotAgreeToTerms;
import com.pathfinder.server.global.exception.memberexception.MemberNotFoundException;
import com.pathfinder.server.member.dto.MemberDto;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.member.repository.MemberRepository;
import com.pathfinder.server.reward.entity.Reward;
import com.pathfinder.server.reward.repository.RewardRepository;
import com.pathfinder.server.reward.service.RewardService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RewardRepository rewardRepository;
    private final RewardService rewardService;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, RewardRepository rewardRepository, RewardService rewardService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.rewardRepository = rewardRepository;
        this.rewardService = rewardService;
    }

    @Transactional
    public Long signup(MemberDto.Post request) {
        if(!request.getAgreeToTerms()){
            throw new MemberNotAgreeToTerms();
        }
        String mail = request.getEmail();
        String name = request.getName();

        verifyExistsEmail(mail);
        verifyExistsName(name);

        //TODO email인증 로직 추가

        Member member = createMember(request);

        List<Reward> initialRewards = rewardService.createInitRewards(); // 초기 보상 리스트 생성
        for (Reward reward : initialRewards) {
            reward.setMember(member); // 각 보상에 해당 멤버 연결
        }
        rewardRepository.saveAll(initialRewards);

        return memberRepository.save(member).getMemberId();
    }

    public Member createMember(MemberDto.Post request) {
        return Member.createMember(
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getAgreeToTerms()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Member updateMember(Member member) {
        Member findMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getPassword())
                .ifPresent(password -> findMember.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(member.getProfileImageUrl())
                .ifPresent(image -> findMember.setProfileImageUrl(image));
        Optional.ofNullable(member.getIntroduce())
                .ifPresent(introduce -> findMember.setIntroduce(introduce));

        return memberRepository.save(findMember);
    }

    @Transactional(readOnly = true)
    public Member findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    public void deleteMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }

    public Member updateProfileImage(Long memberId, Long rewardId) {
        Member findMember = findVerifiedMember(memberId);
        Reward chooseReward = rewardService.findReward(rewardId);
        String imageUrl = chooseReward.getImageUrl();
        Optional.ofNullable(findMember.getProfileImageUrl())
                .ifPresent(image -> findMember.setProfileImageUrl(imageUrl));

        return memberRepository.save(findMember);
    }

    @Transactional(readOnly = true)
    public Member findVerifiedMember(Long memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        Member findMember =
                optionalMember.orElseThrow(() ->
                        new MemberNotFoundException());
        return findMember;
    }

    private void verifyExistsName(String name) {
        Optional<Member> member = memberRepository.findByName(name);
        if (member.isPresent()) {
            throw new MemberNameExistException();
        }
    }

    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new MemberEmailExistException();
        }
    }

}
