package com.hp.repository;


import com.hp.model.PushRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 推送请求存储仓库接口
 * Created by yaoyasong on 2016/5/3.
 */
@Repository
public interface PushRequestRepository extends JpaRepository<PushRequest,String>,JpaSpecificationExecutor<PushRequest> {

    PushRequest findBySenderUid(String senderUid);

}
