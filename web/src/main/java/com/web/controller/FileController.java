package com.web.controller;

import com.commonutils.util.json.JSONObject;
import com.commonutils.util.validate.FileTypeCensor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
@RequestMapping(value = "/file")
public class FileController {
    /**
     * �ļ��ϴ�
     * @param multipartFiles
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")

    public String uploadFile(@RequestParam(value = "uploadFile", required = false) MultipartFile[] multipartFiles,
                                 String param,
                                 HttpServletRequest request){
        JSONObject result = new JSONObject();
        try {
            for (int i=0; i< multipartFiles.length; i++){
//                boolean upFlag = FileTypeCensor.isLegalFileType(FileTypeCensor.FILE_SUFFIX.IMAGE.getValue(),multipartFiles[i]);
//                if(!upFlag){
//                    throw new Exception(FileTypeCensor.FILE_SUFFIX.IMAGE.getDesc()+"����Ϊ"+FileTypeCensor.FILE_SUFFIX.IMAGE.getValue());
//                }

                // ���Դ�ļ����� ����ָ�ϴ�ǰ���ļ�����
                System.out.println("uploadFile:" + multipartFiles[i].getOriginalFilename());
                // �����ļ�(MultipartFileתFile)
                String saveRoot = "f:\\";
                File file = new File(saveRoot + multipartFiles[i].getOriginalFilename());
                InputStream in  = multipartFiles[i].getInputStream();
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer,0,4096)) != -1){
                    os.write(buffer,0,n);
                }
                in.close();
                os.close();
            }
            //��������ֶ�
            System.out.println("param:"+ URLDecoder.decode(param,"UTF-8"));
        } catch (Exception e) {
            result.put("msg", e.getMessage());
            result.put("flag", "fail");
        }
        result.put("flag", "success");
        result.put("msg", "success");

        return result.toString();
    }

    /**
     * �ļ�����
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/downFile")
    public void downTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        File file = new File("f:\\test.txt");//�����ļ�
        if (!file.exists()) {
            System.out.println("�ļ�������!");
        } else {
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            // ��ȡҪ���ص��ļ������浽������
            InputStream in = new FileInputStream(file);
            // ���������
            OutputStream out = response.getOutputStream();
            // ����������
            byte buffer[] = new byte[1024];
            int len;
            // ѭ�����������е����ݶ�ȡ������������
            while ((len = in.read(buffer)) > 0) {
                // ��������������ݵ��������ʵ���ļ�����
                out.write(buffer, 0, len);
            }
            // �ر��ļ�������
            in.close();
            // �ر������
            out.close();
        }
    }

    /**
     * ͼƬ�ϴ�
     * @param multipartFiles
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/imgUpload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")

    public String uploadImg(@RequestParam(value = "uploadFile", required = false) MultipartFile[] multipartFiles,
                             HttpServletRequest request){
        JSONObject result = new JSONObject();
        try {
            for (int i=0; i< multipartFiles.length; i++){
                BufferedImage image = ImageIO.read(multipartFiles[i].getInputStream());
                if(image == null){
                    throw new Exception("���ϴ�ͼƬ");
                }

                // ���Դ�ļ����� ����ָ�ϴ�ǰ���ļ�����
                System.out.println("uploadFile:" + multipartFiles[i].getOriginalFilename());
                // �����ļ�(MultipartFileתFile)
                String saveRoot = "f:\\";
                File file = new File(saveRoot + multipartFiles[i].getOriginalFilename());
                InputStream in  = multipartFiles[i].getInputStream();
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer,0,4096)) != -1){
                    os.write(buffer,0,n);
                }
                in.close();
                os.close();
            }
            //��������ֶ�
            String name = request.getParameter("name");
            System.out.println("name:"+name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("msg", e.getMessage());
            result.put("flag", "fail");
        }
        result.put("flag", "success");
        result.put("msg", "success");

        return result.toString();
    }

    /**
     * excel�ϴ�
     * @param multipartFiles
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/excelUpload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")

    public String uploadExcel(@RequestParam(value = "uploadFile", required = false) MultipartFile[] multipartFiles,
                            HttpServletRequest request){
        JSONObject result = new JSONObject();
        try {
            for (int i=0; i< multipartFiles.length; i++){
                Workbook wb = WorkbookFactory.create(multipartFiles[i].getInputStream());

                // ���Դ�ļ����� ����ָ�ϴ�ǰ���ļ�����
                System.out.println("uploadFile:" + multipartFiles[i].getOriginalFilename());
                // �����ļ�(MultipartFileתFile)
                String saveRoot = "f:\\";
                File file = new File(saveRoot + multipartFiles[i].getOriginalFilename());
                InputStream in  = multipartFiles[i].getInputStream();
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer,0,4096)) != -1){
                    os.write(buffer,0,n);
                }
                in.close();
                os.close();
            }
            //��������ֶ�
            String name = request.getParameter("name");
            System.out.println("name:"+name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("msg", e.getMessage());
            result.put("flag", "fail");
        }
        result.put("flag", "success");
        result.put("msg", "success");

        return result.toString();
    }
}

