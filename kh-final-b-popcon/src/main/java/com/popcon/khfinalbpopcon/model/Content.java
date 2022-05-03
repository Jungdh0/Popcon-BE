package com.popcon.khfinalbpopcon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @Column(name = "content_code")
    private Long contentCode;

    @Column(name = "ott_name")
    private String ottName;

    @Column(name = "ott_img")
    private String ottImg;

    @Column(name = "tag_name") //검색
    private String tagName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content_img_ver")
    private String contentImgVer;

    @Column(name = "content_img_hor")
    private String contentImgHor;

    @Column(name = "content_name") //검색
    private String contentName;

    @Column(name = "release_year")
    private String releaseYear;

    @Column(name = "age_rating")
    private String ageRating;

    @Column(name = "running_time")
    private String runningTime;

    @Column(name = "director")
    private String director;

    @Column(name = "actor")
    private String actor;

    @Column(name = "content_plot")
    private String contentPlot;

    @Column(name = "seasons_info")
    private String seasonsInfo;

    @Column(name = "details_view_count")
    private Integer detailsViewCount;
}
