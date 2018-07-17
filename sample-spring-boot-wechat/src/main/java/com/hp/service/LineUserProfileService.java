package com.hp.service;

import com.hp.model.LineUserProfile;
import com.hp.repository.LineUserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class LineUserProfileService {

    @Autowired
    private LineUserProfileRepository lineUserProfileRepository;

    public LineUserProfile getLineUserProfile(String userId){
        return lineUserProfileRepository.findByUserId(userId);
    }

    public void save(LineUserProfile lineUserProfile){
        lineUserProfileRepository.save(lineUserProfile);
    }

    public List<LineUserProfile> findAll(){
        return lineUserProfileRepository.findAll();
    }

    public List<LineUserProfile> findExistsUserId(Pageable pageable){
        return lineUserProfileRepository.findAll((root, query, cb) -> cb.isNotNull(root.get("userId")),pageable).getContent();
    }
}
