package com.commonutils.util.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author tzs
 * @version 1.0
 * @description ������
 * @since 2019/4/16
 */
public class DownLoadUtil {
    /**
     * ������Url�������ļ�
     * @param urlStr ������Դ·��
     * @param fileName  �����ļ���
     * @param savePath  ����Ŀ¼
     * @return  ������ļ�
     * @throws IOException
     */
    public static File downLoadByUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //���ó�ʱ��Ϊ3��
        conn.setConnectTimeout(3*1000);
        //��ֹ���γ���ץȡ������403����
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //�õ�������
        InputStream inputStream = conn.getInputStream();
        //��ȡ�ֽ�����
        byte[] getData = readInputStream(inputStream);
        //�ļ�����λ��
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream !=null){
            inputStream.close();
        }
        return file;
    }

    /**
     * ���������л�ȡ�ֽ�����
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
