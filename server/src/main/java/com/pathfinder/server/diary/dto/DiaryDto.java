package com.pathfinder.server.diary.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class DiaryDto {
    @Getter
    public static class Post {
        private Long memberId;
        private String title;
        private String content;
        private String area1;
        private String area2;
    }
    @Getter
    public static class Patch {
        private Long diaryId;
        private String title;
        private String content;
        private String area1;
        private String area2;

        public void setDiaryId(Long diaryId) {
            this.diaryId = diaryId;
        }
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long diaryId;
        private String name;
        private String title;
        private String content;
        private String area1;
        private String area2;
        private Long recommendedCount;
//        private Long scrapCount;
        private Integer views;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private boolean isRecommend;
        public void setRecommend(boolean isRecommend) {
            this.isRecommend = isRecommend;
        }
    }


}
