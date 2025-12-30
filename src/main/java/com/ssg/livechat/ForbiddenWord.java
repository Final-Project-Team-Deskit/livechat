package com.ssg.livechat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "forbidden_word")
@Getter
@NoArgsConstructor
public class ForbiddenWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wordId;

    @Column(length = 50, nullable = false)
    private String word;

    @Column(length = 50, nullable = false)
    private String replacement;
}