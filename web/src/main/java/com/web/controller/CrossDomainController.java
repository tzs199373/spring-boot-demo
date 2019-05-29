package com.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping(value = "/cross")
public class CrossDomainController {

    @RequestMapping(value="/getData")
    public void getData(HttpServletRequest request,HttpServletResponse response){
        String callback = request.getParameter("callback");
        String data = "{\"flag\":true,\"msg\":\"success\"}";//���ص�json��ʽ����
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            out.write(callback+"("+data+")");//����jsonp��ʽ�����ݣ����ص�������(json����)
        }
    }
}

