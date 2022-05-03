package com.popcon.khfinalbpopcon.repository;

import com.popcon.khfinalbpopcon.model.UserContentList;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserContentListRepository extends JpaRepository<UserContentList, Long> {

    UserContentList findByUserCodeAndContentCode(Long userCode, Long contentCode);

    List<UserContentList> findByUserCode(Long userCode, Sort sort);
}
