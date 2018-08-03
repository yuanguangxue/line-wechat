package com.hp.web;

import com.hp.model.LineUserProfile;
import com.hp.service.LineUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userProfile")
public class LineUserProfileController {

    @Autowired
    private LineUserProfileService lineUserProfileService;
    /**
     * 创建app
     * @return
     */
    @RequestMapping(value="/", method= RequestMethod.POST)
    public LineUserProfile addUserProfile(LineUserProfile lineUserProfile) {
        lineUserProfileService.save(lineUserProfile);
        return lineUserProfile;
    }

    /**
     * 获取app
     * @return
     */
    @RequestMapping(value="/{userId}", method= RequestMethod.GET)
    public LineUserProfile getLineUserProfile(@PathVariable String userId) {
        return lineUserProfileService.getLineUserProfile(userId);
    }

}
