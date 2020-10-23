package com.commonutils.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    /**
     * ѹ��ָ���ļ����ļ���
     *
     * @param zipParentPath    ѹ�����ļ��洢·��
     * @param zipFilename       ѹ���ļ���
     * @param afterDelete       ѹ����Դ�ļ����ļ����Ƿ�ɾ��
     * @param sourceFiles       Դ�ļ����ļ���
     *
     *tips:
     *1�����ѹ������Ҫ��Ŀ¼�����԰�Դ�ļ������е�һ����Ŀ¼����Ϊ��Σ�sourceFiles = file.listFiles()
     */
    public static void compressToZip(String zipParentPath, String zipFilename,boolean afterDelete,File ... sourceFiles) {
        File zipParentFile = new File(zipParentPath);
        if (!zipParentFile.exists()) {
            zipParentFile.mkdirs();
        }
        File zipFile = new File(zipParentPath + File.separator + zipFilename);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File sourceFile:sourceFiles) {
                writeZip(sourceFile, "", zos);
                if(afterDelete){
                    boolean flag = deleteDir(sourceFile);
                    logger.info("ɾ����ѹ���ļ�[" + sourceFile + "]��־��{}", flag);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * ���������ļ���ѹ��
     * @param sourceFile        Դ�ļ�Ŀ¼
     * @param parentPath        ѹ���ļ�Ŀ¼
     * @param zos               �ļ���
     */
    private static void writeZip(File sourceFile, String parentPath, ZipOutputStream zos) {
        if (sourceFile.isDirectory()) {
            //Ŀ¼
            parentPath += sourceFile.getName() + File.separator;
            File[] files = sourceFile.listFiles();
            for (File f : files) {
                writeZip(f, parentPath, zos);
            }
        } else {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
                ZipEntry zipEntry = new ZipEntry(parentPath + sourceFile.getName());
                zos.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024 * 10];
                while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, len);
                    zos.flush();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * ɾ���ļ���
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        //ɾ�����ļ���
        return dir.delete();
    }

}

