package com.pathfinder.server.diary.entity;


import com.pathfinder.server.audit.Auditable;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.recommend.entity.Recommend;
//import com.pathfinder.server.scrap.entity.Scrap;
import com.pathfinder.server.scrap.entity.Scrap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 65535, nullable = false)
    private String content;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String area1; // 도

    @Column(nullable = false)
    private String area2; // 시

    @Column
    private long recommendedCount;

    @Column
    private Integer views = 0;

    @Column
    private Integer scrapCount = 0;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<Recommend> recommends = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
    }

    public void setRecommend(Recommend recommend) {
        recommends.add(recommend);
        if (recommend.getDiary() != this) {
            recommend.setDiary(this);
        }
    }

}