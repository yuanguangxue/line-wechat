package com.hp.service;

import com.hp.model.LineMessage;
import com.hp.repository.LineMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class LineMessageService {

    @Autowired
    private LineMessageRepository lineMessageRepository;

    public void save(LineMessage lineMessage){
        lineMessageRepository.save(lineMessage);
    }

    public List<LineMessage> findAll(){
        return lineMessageRepository.findAll();
    }

    public LineMessage findOne(String id){
        return lineMessageRepository.findOne(id);
    }

    public List<LineMessage> findAll(Map<String,Object> queryParams){
        return lineMessageRepository.findAll(getQuery(queryParams));
    }

    public Page<LineMessage> findAll(Map<String,Object> queryParams, Pageable pageable){
        return lineMessageRepository.findAll(getQuery(queryParams),pageable);
    }

    @SuppressWarnings({"uncheck","rawtypes"})
    private Specification<LineMessage> getQuery(Map<String, Object> params) {
        return (Root<LineMessage> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder) ->{
            Predicate predicate = builder.equal(root.get("status"),"0");
            for (String key : params.keySet()) {
                predicate = builder.and(predicate,builder.like(root.get(key),(String)params.get(key)));
            }
            return predicate;
        };
    }
}
