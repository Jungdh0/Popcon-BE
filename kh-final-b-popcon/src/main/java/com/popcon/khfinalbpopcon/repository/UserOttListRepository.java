package com.popcon.khfinalbpopcon.repository;

import com.popcon.khfinalbpopcon.model.UserOttList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOttListRepository extends JpaRepository<UserOttList, Long> {

    List<UserOttList> findByUserCode(Long userCode);

    UserOttList findByUserCodeAndOttName(Long userCode, String ottName);
}
