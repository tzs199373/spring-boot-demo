package com.web.aop;

import org.springframework.stereotype.Component;

@Component
public class Target {
    public String targetMethod(String str){
        System.out.println("Ŀ�귽����"+str);
        return str;
    }
}
