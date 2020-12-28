package com.web.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/websocket/{id}")
public class WebSocketServer {
    private static AtomicInteger onlineCount = new AtomicInteger();
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();
    private static Logger log = LogManager.getLogger(WebSocketServer.class);

    /**
     * ���ӽ����ɹ����õķ���
     * */
    @OnOpen
    public void onOpen(@PathParam(value = "id") String id, Session session) throws IOException {
        sessionPools.put(id, session);
        log.info("�û�"+id+"���룡��ǰ��������Ϊ" + addOnlineCount());
        sendMessage(session,"���ӳɹ�");
    }

    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(@PathParam(value = "id") String id) {
        sessionPools.remove(id);
        log.info("��һ���ӹرգ���ǰ��������Ϊ" + subOnlineCount());
    }

    /**
     * �յ��ͻ�����Ϣ����õķ���
     * */
    @OnMessage
    public void onMessage(@PathParam(value = "id") String id,String message,Session session) throws IOException {
        System.err.println("���Կͻ��˵���Ϣ:" + message);
        log.info("���Կͻ��˵���Ϣ:" + message);
        //�����Լ�Լ���ַ������ݣ����� ����|0 ��ʾ��ϢȺ��������|X ��ʾ��Ϣ����idΪX���û�
        String sendMessage = message.split("[|]")[0];
        String sendUserId = message.split("[|]")[1];
        if(sendUserId.equals("0")){
            sendtoAll(sendMessage);
        } else{
            sendtoUser(sendMessage,sendUserId,id);
        }
    }

    /**
     *�Ự����������õķ���
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("��������");
        error.printStackTrace();
    }

    /**
     * ������Ϣ��ָ��ID�û�������û��������򷵻ز�������Ϣ���Լ�
     */
    private void sendtoUser(String message,String sendUserId,String id) throws IOException {
        Session session = sessionPools.get(sendUserId);
        if (session != null) {
            if(!id.equals(sendUserId)){
                sendMessage(session, "�û�" + id + "������Ϣ��" + " <br/> " + message);
            }else{
                sendMessage(session,message);
            }
        } else {
            //����û��������򷵻ز�������Ϣ���Լ�
            sendtoUser("��ǰ�û�������",id,id);
        }
    }

    /**
     * ������Ϣ��������
     */
    private void sendtoAll(String message) throws IOException {
        for (String key : sessionPools.keySet()) {
            try {
                sendMessage(sessionPools.get(key),message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ������Ϣ
     */
    private void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount.get();
    }
    private static synchronized int addOnlineCount() { return onlineCount.incrementAndGet(); }
    private static synchronized int subOnlineCount() {
        return onlineCount.decrementAndGet();
    }

}