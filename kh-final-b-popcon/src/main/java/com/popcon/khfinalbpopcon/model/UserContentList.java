package com.popcon.khfinalbpopcon.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_content_list")
public class UserContentList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_content_index")
    private Long userContentIndex;

    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "content_code")
    private Long contentCode;

    @Builder
    public UserContentList(Long userCode, Long contentCode) {
        this.userCode = userCode;
        this.contentCode = contentCode;
    }
}
