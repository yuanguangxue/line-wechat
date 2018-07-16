package com.hp.repository;

import com.hp.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserInfo,String>,JpaSpecificationExecutor<UserInfo> {
}
