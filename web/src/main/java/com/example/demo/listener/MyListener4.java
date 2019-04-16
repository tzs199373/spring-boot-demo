package com.example.demo.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener4 {
    @EventListener
    public void listener(MyEvent event) {
        System.out.println(String.format("%s�������¼�Դ��%s.", MyListener4.class.getName(), event.getSource()));
    }
}

