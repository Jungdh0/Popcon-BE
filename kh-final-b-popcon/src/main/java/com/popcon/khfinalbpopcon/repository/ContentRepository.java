package com.popcon.khfinalbpopcon.repository;

import com.popcon.khfinalbpopcon.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Content findByContentCode(Long contentCode);

    @Query(value = "SELECT c FROM Content c WHERE c.contentName LIKE %:contentName%")
    List<Content> findByContentNameContains(@Param("contentName") String contentName);

    @Query(value = "SELECT c FROM Content c WHERE c.tagName IN :names")
    List<Content> findByTagNameIn(@Param("names") List<String> names);

    List<Content> findByTagName(String tagName);

}
