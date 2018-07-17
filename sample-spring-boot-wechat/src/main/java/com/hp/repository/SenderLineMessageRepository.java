package com.hp.repository;

import com.hp.model.SenderLineMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderLineMessageRepository extends JpaRepository<SenderLineMessage,String>,JpaSpecificationExecutor<SenderLineMessage> {

}
