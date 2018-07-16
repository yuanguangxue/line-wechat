package com.hp.util;



import org.hibernate.Criteria;

import java.util.regex.Pattern;

/**
 * Criteria构造辅助工具
 * Created by yaoyasong on 2016/5/7.
 */
public final class CriteriaCreator {

    public static Criteria like(String parameter, String value) {
        Pattern pattern = Pattern.compile("^.*" + value + ".*$");
        return null;
        //return Criteria.(parameter).regex(pattern);
    }
}
