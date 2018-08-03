package com.hp.asc.custom.biz.service;

import org.springframework.stereotype.Service;

@Service("dkfServiceHistoryBizService")
public class DkfServiceHistoryBizService {

    public boolean exists(String openid) {
        // return this.map.containsKey(openid);
       /* if (this.dkfServiceHistoryDao.findByOpenidAndIsClosed(openid,
                DkfServiceHistory.UNCLOSED) != null) {
            return true;
        } else {
            return false;
        }*/
        return true;
    }

}
