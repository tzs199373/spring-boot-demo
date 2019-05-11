package com.web.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Aspects {

    @Pointcut("execution(public * com.web.aop.Target.*(..))")
    public void pointCut(){};

    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName() +"��ǰ��֪ͨ��������"+ joinPoint.getArgs()[0]);
    }

    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName() +"�ĺ���֪ͨ��������"+ joinPoint.getArgs()[0]);
    }

    @AfterReturning(value = "pointCut()" , returning = "result" )
    //ע��JoinPoint����һ��Ҫ���ڵ�һ��
    public void logReturn(JoinPoint joinPoint,Object result){
        System.out.println(joinPoint.getSignature().getName() +"�ĺ��÷���֪ͨ������ֵ��"+result);
    }

    @AfterThrowing(value = "pointCut()" , throwing = "exception")
    public void logException(Exception exception){
        System.out.println("�����쳣֪ͨ:"+exception);
    }

    @Around(value = "pointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println(joinPoint.getSignature().getName()+"��joinpoint.proceed֮ǰ�Ļ���֪ͨ,��׽��������"+(String) joinPoint.getArgs()[0]);

        // ִ�б����ط�����Ŀ�귽�������˴����޸���Σ������շ���ִ�к�ķ���ֵ
        Object rvt = joinPoint.proceed(new String[] { (String) joinPoint.getArgs()[0]+",����֪ͨproceedʱ���޸�"});

        System.out.println(joinPoint.getSignature().getName()+"��joinpoint.proceed֮��Ļ���֪ͨ");

        //�ɶ�Ŀ�귽������ֵ�����޸ģ����غ�֪ͨ��������objΪ�������ķ���ֵ(���������޷���ֵ,objΪnull)
        return rvt + "������returnʱ���޸�";
    }
}

/**
 * ���
 targetMethod��joinpoint.proceed֮ǰ�Ļ���֪ͨ,��׽��������hello
 targetMethod��ǰ��֪ͨ��������hello,����֪ͨproceedʱ���޸�
 Ŀ�귽����hello,����֪ͨproceedʱ���޸�
 targetMethod��joinpoint.proceed֮��Ļ���֪ͨ
 targetMethod�ĺ���֪ͨ
 targetMethod�ĺ��÷���֪ͨ������ֵ��hello,����֪ͨproceedʱ���޸ģ�����returnʱ���޸�
 */