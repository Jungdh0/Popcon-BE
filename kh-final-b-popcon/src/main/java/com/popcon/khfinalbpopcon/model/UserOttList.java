package com.popcon.khfinalbpopcon.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_ott_list")
public class UserOttList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ott_index")
    private Long userOttIndex;

    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "ott_name")
    private String ottName;

    @Builder
    public UserOttList(Long userCode, String ottName){
        this.userCode = userCode;
        this.ottName = ottName;
    }
}
