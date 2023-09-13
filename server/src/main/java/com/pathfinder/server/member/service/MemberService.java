package com.pathfinder.server.member.service;

import com.pathfinder.server.email.service.DataService;
import com.pathfinder.server.email.service.MailService;
import com.pathfinder.server.exception.BusinessLogicException;
import com.pathfinder.server.exception.ExceptionCode;
import com.pathfinder.server.global.exception.emailexception.EmailAuthNotAttemptException;
import com.pathfinder.server.global.exception.emailexception.EmailAuthNotCompleteException;
import com.pathfinder.server.global.exception.memberexception.MemberNotAgreeToTerms;
import com.pathfinder.server.global.exception.memberexception.MemberNotFoundException;
import com.pathfinder.server.member.dto.MemberDto;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final DataService dataService;

    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    @Value("${spring.mail.email-complete-expiration-millis}")
    private long emailCompleteExpirationMillis;

    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         MailService mailService,
                         DataService dataService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.dataService = dataService;
    }
    @Transactional
    public Long signup(MemberDto.Post request) {

        verifyExistsEmail(request.getEmail());
        checkEmailAuthComplete(request.getEmail());

        if (!request.getAgreeToTerms()) {
            throw new IllegalArgumentException("약관에 동의해야 회원 가입이 가능합니다.");
        }

        Member member = createMember(request);

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

    @Transactional(readOnly = true)
    public Member findVerifiedMember(Long memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        Member findMember =
                optionalMember.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (!findMember.getAgreeToTerms()) {    // 회원가입 약관 미동의 계정
            new MemberNotAgreeToTerms();
        }
        return findMember;
    }

    private void verifyExistsName(String name) {
        Optional<Member> member = memberRepository.findByName(name);
        if (member.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NAME_EXISTS);
        }
    }

    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_EXISTS);
        }
    }

    public boolean checkCode(String toEmail, String givenCode) {

        boolean isValid = checkCodeValid(toEmail, givenCode);

        if(isValid) {
            saveTrueInRedis(toEmail);
        }

        return isValid;
    }

    private boolean checkCodeValid(String toEmail, String givenCode){

        String savedCode = dataService.getValues(AUTH_CODE_PREFIX + toEmail);

        if(savedCode == null) throw new EmailAuthNotAttemptException();

        return savedCode.equals(givenCode);
    }

    private void saveTrueInRedis(String toEmail) {
        dataService.saveValues(
                AUTH_CODE_PREFIX + toEmail,
                "true",
                Duration.ofMillis(emailCompleteExpirationMillis));
    }

    public void sendFindPasswordCodeToEmail(String toEmail) {

        verifiedMember(toEmail);

        String authCode = mailService.sendAuthEmail(toEmail);

        saveCodeInRedis(toEmail, authCode);
    }

    private Member verifiedMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void saveCodeInRedis(String toEmail, String authCode) {
        dataService.saveValues(
                AUTH_CODE_PREFIX + toEmail,
                authCode,
                Duration.ofMillis(authCodeExpirationMillis));
    }

    private void checkEmailAuthComplete(String email) {
        if(dataService.getValues(AUTH_CODE_PREFIX + email) == null || !dataService.getValues(AUTH_CODE_PREFIX + email).equals("true")){
            throw new EmailAuthNotCompleteException();
        }

        dataService.deleteValues(AUTH_CODE_PREFIX + email);
    }
}
