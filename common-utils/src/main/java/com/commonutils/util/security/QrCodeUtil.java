package com.commonutils.util.security;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QrCodeUtil {
    private static final int BLACK = Color.black.getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int DEFAULT_QR_SIZE = 183;
    private static final String DEFAULT_QR_FORMAT = "png";
    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * ���ɴ�ͼƬ�Ķ�ά��
     * @param content  ��ά����Ҫ��������Ϣ
     * @param size  ��С
     * @param extension  �ļ���ʽ��չ
     * @param insertImg  �м��logoͼƬ
     * @return
     */
    public static byte[] createQrCode(String content, int size, String extension, Image insertImg) {
        if (size <= 0) {
            throw new IllegalArgumentException("size (" + size + ")  cannot be <= 0");
        }
        ByteArrayOutputStream baos = null;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            //ʹ����Ϣ����ָ����С�ĵ���
            BitMatrix m = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            //ȥ���ױ�
            m = updateBit(m, 0);

            int width = m.getWidth();
            int height = m.getHeight();

            //��BitMatrix�е���Ϣ���õ�BufferdImage�У��γɺڰ�ͼƬ
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, m.get(i, j) ? BLACK : WHITE);
                }
            }
            if (insertImg != null) {
                // �����м��logoͼƬ
                insertImage(image, insertImg, m.getWidth());
            }
            //����Ϊȥ�ױ߶���С��ͼƬ�ٷŴ�
            image = zoomInImage(image, size, size);
            baos = new ByteArrayOutputStream();
            ImageIO.write(image, extension, baos);

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(baos != null)
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return EMPTY_BYTES;
    }

    public static byte[] createQrCode(String content, int size, String extension) {
        return createQrCode(content, size, extension, null);
    }

    public static byte[] createQrCode(String content) {
        return createQrCode(content, DEFAULT_QR_SIZE, DEFAULT_QR_FORMAT);
    }

    /**
     * �Զ����ά��ױ߿��
     * @param matrix
     * @param margin
     * @return
     */
    private static BitMatrix updateBit(BitMatrix matrix, int margin) {
        int tempM = margin * 2;
        int[] rec = matrix.getEnclosingRectangle(); // ��ȡ��ά��ͼ��������
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // �����Զ���߿������µ�BitMatrix
        resMatrix.clear();
        for (int i = margin; i < resWidth - margin; i++) { // ѭ��������ά��ͼ�����Ƶ��µ�bitMatrix��
            for (int j = margin; j < resHeight - margin; j++) {
                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    // ͼƬ�Ŵ���С
    public static BufferedImage zoomInImage(BufferedImage originalImage, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return newImage;
    }

    private static void insertImage(BufferedImage source, Image insertImg, int size) {
        try {
            int width = insertImg.getWidth(null);
            int height = insertImg.getHeight(null);
            width = width > size / 6 ? size / 6 : width;  // logo��Ϊ��ά�������֮һ��С
            height = height > size / 6 ? size / 6 : height;
            Graphics2D graph = source.createGraphics();
            int x = (size - width) / 2;
            int y = (size - height) / 2;
            graph.drawImage(insertImg, x, y, width, height, null);
            Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
            graph.setStroke(new BasicStroke(3f));
            graph.draw(shape);
            graph.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try {
            FileOutputStream fos = new FileOutputStream("F:\\QRcode.png");//�������ɵ�·��
            fos.write(createQrCode("test"));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
