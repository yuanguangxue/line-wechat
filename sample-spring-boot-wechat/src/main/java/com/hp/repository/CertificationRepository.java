package com.hp.repository;


import com.hp.model.PushCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * 证书存储仓库
 * Created by yaoyasong on 2016/5/6.
 */
public interface CertificationRepository extends JpaRepository<PushCertification,String>,JpaSpecificationExecutor<PushCertification> {
    PushCertification findByAppId(String appId);
}
