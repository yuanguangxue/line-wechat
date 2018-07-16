package com.hp.repository;


import com.hp.model.PushCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * 证书存储仓库
 * Created by yaoyasong on 2016/5/6.
 */
@Repository
public interface CertificationRepository extends JpaRepository<PushCertification,String>,JpaSpecificationExecutor<PushCertification> {
    PushCertification findByAppId(String appId);
}
