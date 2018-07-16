package com.hp.repository;

import com.hp.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Application 仓库
 * Created by yaoyasong on 2016/5/11.
 */
public interface AppRepository extends JpaRepository<Application,String>,JpaSpecificationExecutor<Application> {

}
