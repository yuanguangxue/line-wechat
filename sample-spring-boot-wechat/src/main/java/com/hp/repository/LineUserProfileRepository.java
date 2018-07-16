package com.hp.repository;

import com.hp.model.LineUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LineUserProfileRepository extends JpaRepository<LineUserProfile,String>,JpaSpecificationExecutor<LineUserProfile> {
    LineUserProfile findByUserId(String userId);
}
