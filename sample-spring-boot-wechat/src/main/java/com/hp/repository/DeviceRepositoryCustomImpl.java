package com.hp.repository;

import com.hp.model.UserDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户设备自定义查询仓库接口实现
 * Created by yaoyasong on 2016/5/4.
 */
@Repository
public class DeviceRepositoryCustomImpl implements DeviceRepositoryCustom<UserDevice> {

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<UserDevice> findAll(Map<String, Object> params) {
        Specification<UserDevice> specification = getQuery(params);
        return deviceRepository.findAll(specification);
    }

    @SuppressWarnings({"uncheck","rawtypes"})
    private Specification<UserDevice> getQuery(Map<String, Object> params) {
        return (Root<UserDevice> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder) ->{
            Predicate predicate = builder.equal(root.get("status"),"1");
            for (String key : params.keySet()) {
                if (key.equals("alias")) {
                    Object o = params.get(key);
                    if(o instanceof Collection){
                        CriteriaBuilder.In<String> in = builder.in(root.get("alias"));
                        Collection collection = (Collection) o;
                        for (Object value : collection){
                            in.value((String)value);
                        }
                        predicate = builder.and(predicate,in);
                    }
                } else {
                    predicate = builder.and(predicate,builder.like(root.get(key),(String)params.get(key)));
                }
            }
            return predicate;
        };
    }

    @Override
    public Page<UserDevice> findAll(Map<String, Object> params, Pageable pageable) {
        Specification<UserDevice> specification = getQuery(params);
         return deviceRepository.findAll(specification,pageable);
    }
}
