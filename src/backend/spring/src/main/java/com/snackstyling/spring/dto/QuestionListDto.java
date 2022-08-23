package com.snackstyling.spring.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuestionListDto {
    private Long qid;
    private Long mid;
    private String nickname;
    private Integer weight;
    private Integer height;
    private LocalDate end_date;
    private LocalDateTime post_date;
    private String tpo;
    private String comments;
    private Long ans_count;
}
