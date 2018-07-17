package com.hp.service;

import com.hp.model.LineUserProfile;
import com.hp.repository.LineUserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
