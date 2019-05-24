package com.commonutils.util.validate;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description: �����ϴ�����,У���Ƿ�Ϸ�
 * �ڷ��������ж��ļ����͵����⣬���û�ȡ�ļ�ͷ�ķ�ʽ��
 * ֱ�Ӷ�ȡ�ļ���ǰ�����ֽڣ����ж��ϴ��ļ��Ƿ���ϸ�ʽ
 * @author: huangyawei
 * @Created 2013 2013-8-19����18:58:15
 */
public class FileTypeCensor {
    //��¼�����ļ�ͷ��Ϣ����Ӧ���ļ�����
    public static Map<String, String> mFileTypes = new HashMap<String, String>();

    static {
        // images
        mFileTypes.put("FFD8FFE0", ".jpg");//FFD8FF
        mFileTypes.put("89504E47", ".png");//FFD8FFE1
        mFileTypes.put("47494638", ".gif");
        mFileTypes.put("49492A00", ".tif");
        mFileTypes.put("424D", ".bmp");

        //PS��CAD
        mFileTypes.put("38425053", ".psd");
        mFileTypes.put("41433130", ".dwg"); // CAD
        mFileTypes.put("252150532D41646F6265",".ps");

        //�칫�ĵ���
        mFileTypes.put("D0CF11E0", ".doc"); //ppt��doc��xls
        mFileTypes.put("504B0304", ".docx");//pptx��docx��xlsx

        /**ע�������ı��ĵ�¼�����ݹ��࣬���ȡ�ļ�ͷʱ��Ϊ���-START**/
        mFileTypes.put("0D0A0D0A", ".txt");//txt
        mFileTypes.put("0D0A2D2D", ".txt");//txt
        mFileTypes.put("0D0AB4B4", ".txt");//txt
        mFileTypes.put("B4B4BDA8", ".txt");//�ļ�ͷ��Ϊ����
        mFileTypes.put("73646673", ".txt");//txt,�ļ�ͷ��ΪӢ����ĸ
        mFileTypes.put("32323232", ".txt");//txt,�ļ�ͷ������Ϊ����
        mFileTypes.put("0D0A09B4", ".txt");//txt,�ļ�ͷ������Ϊ����
        mFileTypes.put("3132330D", ".txt");//txt,�ļ�ͷ������Ϊ����
        /**ע�������ı��ĵ�¼�����ݹ��࣬���ȡ�ļ�ͷʱ��Ϊ���-END**/


        mFileTypes.put("7B5C727466", ".rtf"); // �ռǱ�

        mFileTypes.put("255044462D312E", ".pdf");

        //��Ƶ����Ƶ��
        mFileTypes.put("3026B275",".wma");
        mFileTypes.put("57415645", ".wav");
        mFileTypes.put("41564920", ".avi");
        mFileTypes.put("4D546864", ".mid");
        mFileTypes.put("2E524D46", ".rm");
        mFileTypes.put("000001BA", ".mpg");
        mFileTypes.put("000001B3", ".mpg");
        mFileTypes.put("6D6F6F76", ".mov");
        mFileTypes.put("3026B2758E66CF11", ".asf");

        //ѹ����
        mFileTypes.put("52617221", ".rar");
        mFileTypes.put("1F8B08", ".gz");

        //�����ļ�
        mFileTypes.put("3C3F786D6C", ".xml");
        mFileTypes.put("68746D6C3E", ".html");
        mFileTypes.put("7061636B", ".java");
        mFileTypes.put("3C254020", ".jsp");
        mFileTypes.put("4D5A9000", ".exe");


        mFileTypes.put("44656C69766572792D646174653A", ".eml"); // �ʼ�
        mFileTypes.put("5374616E64617264204A", ".mdb");//Access���ݿ��ļ�

        mFileTypes.put("46726F6D", ".mht");
        mFileTypes.put("4D494D45", ".mhtml");
    }

    //�Ϸ����ļ���׺,Ĭ�����к�׺�����Ϸ�
    private static String res_fileType=FILE_SUFFIX.getAllValue();

    public enum FILE_SUFFIX {
        IMAGE("ͼƬ",".jpeg.jpg.png"),
        OFFICE("office�ļ�",".ppt.doc.xls.pptx.docx.xlsx"),
        MEDIA("ý���ļ�",".wma.wav.avi.mp3.mp4.mkv"),
        TXT("txt�ļ�",".txt"),
        ZIP("ѹ����",".zip.gz.rar"),
        APPLICATION("�����ļ�",".xml.html.java.jsp.exe");

        private String value;
        private String desc;
        FILE_SUFFIX(String desc,String value) {
            this.desc = desc;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }

        public static String getAllValue() {
            StringBuffer sb = new StringBuffer();
            for (FILE_SUFFIX suffix : FILE_SUFFIX.values()) {
                sb.append(suffix.value);
            }
            return sb.toString();
        }
    }


    /**
     * �����ļ�����������ȡ�ļ�ͷ��Ϣ
     * @param  is �ļ���
     * @return �ļ�ͷ��Ϣ
     */
    public static String getFileType(InputStream  is) {
        byte[] b = new byte[4];
        if(is!=null){
            try {
                is.read(b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFileTypes.get(getFileHeader(b));
    }

    /**
     * �����ļ�ת���ɵ��ֽ������ȡ�ļ�ͷ��Ϣ
     * @param  b �ļ�ת���ɵ��ֽ�����
     * @return �ļ�ͷ��Ϣ
     */
    public static String getFileHeader(byte[] b) {
        String value = bytesToHexString(b);
        return value;
    }

    /**
     * ��Ҫ��ȡ�ļ�ͷ��Ϣ���ļ���byte����ת����string���ͱ�ʾ
     * ������δ�������������ļ���������֤�ķ�����
     * ���ֽ������ǰ��λת����16�����ַ���������ת����ʱ��Ҫ�Ⱥ�0xFF��һ�������㡣
     * ������Ϊ�������ļ������ֽ������У��кܶ��Ǹ�����������������󣬿��Խ�ǰ��ķ���λ��ȥ����
     * ����ת���ɵ�16�����ַ�����ౣ����λ�������������С��10����ôת����ֻ��һλ��
     * ��Ҫ��ǰ�油0����������Ŀ���Ƿ���Ƚϣ�ȡ��ǰ��λ���ѭ���Ϳ�����ֹ��
     * @param src Ҫ��ȡ�ļ�ͷ��Ϣ���ļ���byte����
     * @return �ļ�ͷ��Ϣ
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // ��ʮ�����ƣ����� 16���޷���������ʽ����һ�������������ַ�����ʾ��ʽ����ת��Ϊ��д
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println("�ϴ��ļ�ͷ��Ϣ" + builder.toString());
        return builder.toString();
    }

    /**
     * �ж��ϴ����ļ��Ƿ�Ϸ�
     * ��һ������һ������ļ�����չ����
     * (������ �ڶ�������ļ���MIME���� ��
     * @param attachDoc
     * @return boolean
     */
    public static boolean isLegalFileType(String fileType,FileItem attachDoc){
        boolean upFlag=false;//Ϊ���ʾ�����ϴ�������Ϊ�ٱ�겻����

        if(ObjectCensor.isStrRegular(fileType)){
            res_fileType = fileType;
        }

        if(attachDoc != null){
            String attachName =attachDoc.getName();

            if(ObjectCensor.isStrRegular(attachName)){

                /**�����ڴ��ַ��������ұ߳��ֵ�ָ�����ַ���������   **/
                String sname = attachName.substring(attachName.lastIndexOf("."));

                /**ͳһת��ΪСд**/
                sname=sname.toLowerCase();

                /**��һ��������ļ���չ�����Ƿ����Ҫ��Χ**/
                if(res_fileType.indexOf(sname)!=-1){
                    upFlag=true;
                }

                /**
                 * �ڶ�������ȡ�ϴ��������ļ�ͷ���ж�������������,����ȡ����չ��
                 * ֱ�Ӷ�ȡ�ļ���ǰ�����ֽڣ����ж��ϴ��ļ��Ƿ���ϸ�ʽ
                 * ��ֹ�ϴ����������չ���ƹ�У��
                 ***/
                if(upFlag){
                    byte[] b = new byte[4];
                    String req_fileType = null;//�ļ�ͷӳ�����չ��
                    try {
                        req_fileType = getFileType(attachDoc.getInputStream());
                        System.out.println("�ļ�ͷӳ�����չ��:"+req_fileType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /**��������������ļ�ͷӳ�����չ�����Ƿ����Ҫ��Χ**/
                    if(ObjectCensor.isStrRegular(req_fileType) && !"null".equals(req_fileType)){
                        if(res_fileType.indexOf(req_fileType)!=-1){
                            upFlag=true;
                        }else{
                            upFlag=false;
                        }
                    }else{
                       /**�����ļ�ͷ��mFileTypes��ӳ�䲻���ļ����ͣ����ҷ��У�����У��*/
                        System.out.println("�ļ�ͷ��mFileTypes��ӳ�䲻���ļ����ͣ����ҷ���");
                    }
                }
            }
        }
        return upFlag;
    }

    /**
     * �ж��ϴ����ļ��Ƿ�Ϸ�
     * ��һ������һ������ļ�����չ����
     * (������ �ڶ�������ļ���MIME���� ��
     * @param multipartFile
     * @return boolean
     */
    public static boolean isLegalFileType(String fileType,MultipartFile multipartFile){
        boolean upFlag=false;//Ϊ���ʾ�����ϴ�������Ϊ�ٱ�겻����

        if(ObjectCensor.isStrRegular(fileType)){
            res_fileType = fileType;
        }

        if(multipartFile != null){
            String attachName =multipartFile.getOriginalFilename();

            if(ObjectCensor.isStrRegular(attachName)){

                /**�����ڴ��ַ��������ұ߳��ֵ�ָ�����ַ���������   **/
                String sname = attachName.substring(attachName.lastIndexOf("."));

                /**ͳһת��ΪСд**/
                sname=sname.toLowerCase();

                /**��һ��������ļ���չ�����Ƿ����Ҫ��Χ**/
                if(res_fileType.indexOf(sname)!=-1){
                    upFlag=true;
                }

                /**
                 * �ڶ�������ȡ�ϴ��������ļ�ͷ���ж�������������,����ȡ����չ��
                 * ֱ�Ӷ�ȡ�ļ���ǰ�����ֽڣ����ж��ϴ��ļ��Ƿ���ϸ�ʽ
                 * ��ֹ�ϴ����������չ���ƹ�У��
                 ***/
                if(upFlag){
                    byte[] b = new byte[4];
                    String req_fileType = null;//�ļ�ͷӳ�����չ��
                    try {
                        req_fileType = getFileType(multipartFile.getInputStream());
                        System.out.println("�ļ�ͷӳ�����չ��:"+req_fileType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /**��������������ļ�ͷӳ�����չ�����Ƿ����Ҫ��Χ**/
                    if(ObjectCensor.isStrRegular(req_fileType) && !"null".equals(req_fileType)){
                        if(res_fileType.indexOf(req_fileType)!=-1){
                            upFlag=true;
                        }else{
                            upFlag=false;
                        }
                    }else{
                        /**�����ļ�ͷ��mFileTypes��ӳ�䲻���ļ����ͣ����ҷ��У�����У��*/
                        System.out.println("�ļ�ͷ��mFileTypes��ӳ�䲻���ļ����ͣ����ҷ���");
                    }
                }
            }
        }
        return upFlag;
    }
}
