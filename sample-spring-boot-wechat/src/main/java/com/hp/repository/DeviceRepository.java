package com.hp.repository;


import com.hp.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * 用户设备信息保存仓库
 * Created by yaoyasong on 2016/5/3.
 */
public interface DeviceRepository extends JpaRepository<UserDevice,String>,JpaSpecificationExecutor<UserDevice> {

    UserDevice findByAppIdAndNativeToken(String appId, String nativeToken);
}
