package com.snackstyling.spring.community.question.domain;

import com.snackstyling.spring.community.answer.domain.Answer;
import com.snackstyling.spring.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Question {
    @Id @GeneratedValue
    @Column(name="queId")
    private Long id;
    @ManyToOne
    @JoinColumn(name="memberId")
    private Member member;
    private LocalDateTime postDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private Integer tpo;
    @Column(length = 1000)
    private String comments;
    private Integer adopt=0;
    private Integer used=1;
}
