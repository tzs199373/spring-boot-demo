package com.example.demo.listener;

import org.springframework.context.ApplicationListener;

public class MyListener1 implements ApplicationListener<MyEvent> {
    public void onApplicationEvent(MyEvent event) {
        System.out.println(String.format("%s�������¼�Դ��%s.", MyListener1.class.getName(), event.getSource()));
    }
}

