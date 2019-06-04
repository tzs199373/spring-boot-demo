package com.commonutils.util.security;

import com.commonutils.util.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES���ܹ���
 */
public class AESUtil {
    /**
     *
     * @param EncryptRule ��Կ
     * @param Content ��������
     * @return base64����
     */
    public static String Encrypt(String EncryptRule,String Content) {
        try {
            //1.������Կ��������ָ��ΪAES�㷨,�����ִ�Сд
            KeyGenerator Keygen=KeyGenerator.getInstance("AES");

            //2.����Key��ʼ����Կ������
            //����һ��128λ�����Դ,���ݴ�����ֽ�����
            SecureRandom Random = SecureRandom.getInstance("SHA1PRNG");
            Random.setSeed(EncryptRule.getBytes());
            Keygen.init(128, Random);

            //3.����ԭʼ�Գ���Կ
            SecretKey Original_Key=Keygen.generateKey();

            //4.���ԭʼ�Գ���Կ���ֽ�����
            byte [] Raw=Original_Key.getEncoded();

            //5.�����ֽ���������AES��Կ
            SecretKey Key=new SecretKeySpec(Raw, "AES");

            //6.����ָ���㷨AES�Գ�������
            Cipher CipherInstance=Cipher.getInstance("AES");

            //7.��ʼ������������һ������Ϊ����(Encrypt_mode)���߽��ܽ���(Decrypt_mode)�������ڶ�������Ϊʹ�õ�KEY
            CipherInstance.init(Cipher.ENCRYPT_MODE, Key);

            //8.��ȡ�������ݵ��ֽ�����(����Ҫ����Ϊutf-8)��Ȼ��������������ĺ�Ӣ�Ļ�����ľͻ����Ϊ����
            byte [] Byte_Encode=Content.getBytes("utf-8");

            //9.�����������ĳ�ʼ����ʽ--���ܣ������ݼ���
            byte [] Byte_AES=CipherInstance.doFinal(Byte_Encode);

            //10.�����ܺ������ת��Ϊ�ַ���
            String AES_encode= Base64.encodeBase64URLSafeString(Byte_AES);

            //11.���ַ�������
            return AES_encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param DecryptRule ��Կ
     * @param Content   Encrypt�������ɵ�����
     * @return ԭ��
     */
    public static String Decrypt(String DecryptRule,String Content) {
        try {
            //1.������Կ��������ָ��ΪAES�㷨,�����ִ�Сд
            KeyGenerator Keygen=KeyGenerator.getInstance("AES");

            //2.����DecryptRule�����ʼ����Կ������
            //����һ��128λ�����Դ,���ݴ�����ֽ�����
            SecureRandom Random = SecureRandom.getInstance("SHA1PRNG");
            Random.setSeed(DecryptRule.getBytes());
            Keygen.init(128, Random);

            //3.����ԭʼ�Գ���Կ
            SecretKey Original_Key=Keygen.generateKey();

            //4.���ԭʼ�Գ���Կ���ֽ�����
            byte [] Raw=Original_Key.getEncoded();

            //5.�����ֽ���������AES��Կ
            SecretKey Key=new SecretKeySpec(Raw, "AES");

            //6.����ָ���㷨AES�Գ�������
            Cipher CipherInstance=Cipher.getInstance("AES");

            //7.��ʼ������������һ������Ϊ����(Encrypt_mode)���߽���(Decrypt_mode)�������ڶ�������Ϊʹ�õ�KEY
            CipherInstance.init(Cipher.DECRYPT_MODE, Key);

            //8.�����ܲ����������ݽ�����ֽ�����
            byte [] Byte_Content= Base64.decodeBase64(Content);

			/*
			 * ����
			 */
            byte [] Byte_Decode=CipherInstance.doFinal(Byte_Content);
            String AES_decode=new String(Byte_Decode,"utf-8");

            return AES_decode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.element("UserName","18516094389");
        json.element("Password","123456");
        json.element("CellPhone","18516094389");
        json.element("NickName","18516094389");
//        String content = "{\"UserName\":\"18516094389\",\"Password\":\"123456\"}";
        String content = json.toString();
        String password = "S2V5QnlYWFdGcm9tQm9XdVl1bg==";
        System.out.println("����֮ǰ��" + content);

        // ����
        String encrypt = Encrypt(password,content);
        System.out.println("���ܺ�����ݣ�" + encrypt);

        // ����
        String decrypt = Decrypt(password,encrypt);
        System.out.println("���ܺ�����ݣ�" + decrypt);
    }
}
