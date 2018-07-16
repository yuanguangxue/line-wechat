package com.hp.repository;


import com.hp.model.PushMsg;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 推送的待确认消息仓库
 *
 * Created by yaoyasong on 2016/5/10.
 */
@Repository
public interface PushMsgRepository extends JpaRepository<PushMsg,String>,JpaSpecificationExecutor<PushMsg> {
    List<PushMsg> findByAudienceDeviceId(String audienceDeviceId, Sort sort);
}
