package com.hp.service;

import com.hp.model.SenderLineMessage;
import com.hp.repository.SenderLineMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SenderLineMessageService {

    @Autowired
    private SenderLineMessageRepository senderLineMessageRepository;

    public void save(SenderLineMessage senderLineMessage){
        senderLineMessageRepository.save(senderLineMessage);
    }

    public List<SenderLineMessage> findAll(){
        return senderLineMessageRepository.findAll();
    }
}
