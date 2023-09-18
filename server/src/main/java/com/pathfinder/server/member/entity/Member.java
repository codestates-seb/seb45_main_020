package com.pathfinder.server.member.entity;

import com.pathfinder.server.diary.entity.Diary;
import com.pathfinder.server.recommend.entity.Recommend;
import com.pathfinder.server.reward.entity.Reward;
import com.pathfinder.server.scrap.entity.Scrap;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 30, nullable = false, updatable = false)
    private String email;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false)
    private String introduce;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private int diaryCount;

    @Column(nullable = false)
    private Boolean agreeToTerms;   // todo batch로 미동의 회원 사용불가 처리

    // todo 일정기간 미로그인자 휴면상태로 변경

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Recommend> recommends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Reward> rewards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    public void setScrap(Scrap scrap) {
        scraps.add(scrap);
        if(scrap.getMember() != this) {
            scrap.setMember(this);
        }
    }

    public void setRecommend(Recommend recommend) {
        recommends.add(recommend);
        if (recommend.getMember() != this) {
            recommend.setMember(this);
        }
    }

    public void setDiary(Diary diary) {
        diaries.add(diary);
        if (diary.getMember() != this) {
            diary.setMember(this);
        }
    }

    public void setReward(Reward reward) {
        rewards.add(reward);
        if(reward.getMember() != this) {
            reward.setMember(this);
        }
    }

    // 일반 회원가입
    public static Member createMember(String email, String name, String password, boolean agreeToTerms) {
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .introduce("안녕하세요")
                .authority(Authority.ROLE_USER)
                .profileImageUrl("https://main20-pathfinder.s3.ap-northeast-2.amazonaws.com/defaultImage.png")
                .diaryCount(0)
                .agreeToTerms(agreeToTerms)
                .build();
    }

    // oauth2 회원가입
    public static Member createMember(String email, String name, String password) {
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .introduce("안녕하세요")
                .authority(Authority.ROLE_USER)
                .profileImageUrl("https://main20-pathfinder.s3.ap-northeast-2.amazonaws.com/defaultImage.png")
                .diaryCount(0)
                .agreeToTerms(true)
                .build();
    }
}