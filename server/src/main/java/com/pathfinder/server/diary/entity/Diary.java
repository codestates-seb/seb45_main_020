package com.pathfinder.server.diary.entity;


import com.pathfinder.server.audit.Auditable;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.recommend.entity.Recommend;
import com.pathfinder.server.scrap.entity.Scrap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diary extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String area1; // 도

    @Column(nullable = false)
    private String area2; // 시

    @Column
    private long recommendedCount;

    @Column
    private int views;

//    @Column
//    private int scrapCount;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<Recommend> recommends = new ArrayList<>();

//    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
//    private List<Scrap> scraps = new ArrayList<>();

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
