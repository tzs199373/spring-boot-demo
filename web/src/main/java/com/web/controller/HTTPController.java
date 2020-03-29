package com.web.controller;

import com.commonutils.util.http.HttpClientPoolUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HTTPController {
    private static Map<String,String> headMap = new HashMap<String,String>();

    static{
//        headMap.put("Content-Type", "application/json;charset=UTF-8");
//        headMap.put("Content-Type", "application/x-www-form-urlencoded");
    }

    @RequestMapping("/httpTest")
    public String httpTest(@RequestParam String name) throws Exception{
        String result = HttpClientPoolUtil.post("http://127.0.0.1:8900/testUrl","",headMap);
        return result;
    }

    @RequestMapping("/httpTest2")
    public String httpTest2(@RequestParam String name) throws Exception{
        String result = name;
        return result;
    }
}

