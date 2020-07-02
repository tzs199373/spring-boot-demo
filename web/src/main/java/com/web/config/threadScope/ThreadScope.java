package com.web.config.threadScope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * �Զ��屾���̼߳����bean�����򣬲�ͬ���߳��ж�Ӧ��beanʵ���ǲ�ͬ�ģ�ͬһ���߳���ͬ����bean��ͬһ��ʵ��
 */
public class ThreadScope implements Scope {
    //�����������������Ϊһ������thread�������ڶ���bean��ʱ���scopeʹ��
    public static final String SCOPE_THREAD = "thread";

    private ThreadLocal<Map<String, Object>> beanMap = ThreadLocal.withInitial(()->new HashMap<>());

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object bean = beanMap.get().get(name);
        if (Objects.isNull(bean)) {
            bean = objectFactory.getObject();
            beanMap.get().put(name, bean);
        }
        return bean;
    }

    @Nullable
    @Override
    public Object remove(String name) {
        return this.beanMap.get().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        //bean������Χ������ʱ����õķ���������bean����
        System.out.println(name);
    }

    @Nullable
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Nullable
    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}

