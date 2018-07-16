package com.hp.repository;

import com.hp.model.LineMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LineMessageRepository extends JpaRepository<LineMessage,String>,JpaSpecificationExecutor<LineMessage> {

}
