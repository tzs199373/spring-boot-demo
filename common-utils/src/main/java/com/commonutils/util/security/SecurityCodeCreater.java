package com.commonutils.util.security;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *  ����������  ����������֤��ͼƬ��������Ҫ�����������������ó���static<br/>
 */
public class SecurityCodeCreater {

    //ͼƬ�Ŀ��
    private final static int IMAGEWIDTH = 70;
    //ͼƬ�ĸ߶�
    private final static int IMAGEHEIGHT = 30;

    //�����С
    private final static int FONTSIZE = 20;

    //�ַ�������
    private final static int CODE_LENGTH = 4;

    //����ַ���Χ
    private final static char[] CHAR_RANGE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'
    };

//    public static  void main(String[] args)throws Exception
//    {
//
//	     //��main���������������ɵ���֤��ͼ��
//
//	     FileOutputStream fos = new FileOutputStream("c://test.jpg");
//	     JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
//	     encoder.encode(getImage(getRandString()));
//    }


    private static Random random = new Random();

    /**
     * ��������ַ���
     * @return ����ַ���
     */
    public  static String getRandString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++)
        {
            sb.append(CHAR_RANGE[random.nextInt(CHAR_RANGE.length)]);
        }
        return sb.toString();
    }

    /**
     * ���������ɫ
     * @param ll ������ɫֵ����(lower limit)
     * @param ul ������ɫֵ����(upper limit)
     * @return ���ɵ������ɫ����
     */
    private static Color getRandColor(int ll, int ul) {
        if (ll > 255) {
            ll = 255;
        }
        if (ll < 1) {
            ll = 1;
        }
        if (ul > 255) {
            ul = 255;
        }
        if (ul < 1) {
            ul = 1;
        }
        if (ul == ll) {
            ul = ll + 1;
        }
        int r = random.nextInt(ul - ll) + ll;
        int g = random.nextInt(ul - ll) + ll;
        int b = random.nextInt(ul - ll) + ll;
        Color color = new Color(r,g,b);
        return color;
    }

    /**
     * ����ָ���ַ�����ͼ������
     * @param verifyCode ��������ӡ������ַ���
     * @return ���ɵ�ͼ������
     * */
    public static BufferedImage getImage(String verifyCode){
        BufferedImage buffImg = new BufferedImage(IMAGEWIDTH, IMAGEHEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        // ����һ��������������ࡣ
        Random random = new Random();
        // �趨ͼ�񱳾�ɫ(��Ϊ��������������ƫ��)
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, IMAGEWIDTH, IMAGEHEIGHT);
        // �������壬����Ĵ�СӦ�ø���ͼƬ�ĸ߶�������
        Font font = new Font("Times New Roman", Font.HANGING_BASELINE, FONTSIZE);
        // �������塣
        g.setFont(font);
        // ���߿�
        // g.setColor(Color.BLACK);
        g.drawRect(0, 0, IMAGEWIDTH - 1, IMAGEHEIGHT - 1);
        // �������155�������ߣ�ʹͼ���е���֤�벻�ױ���������̽�⵽��
        // g.setColor(Color.GRAY);
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(IMAGEWIDTH);
            int y = random.nextInt(IMAGEHEIGHT);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        // �������4λ���ֵ���֤�롣
        for (int i = 0; i < verifyCode.length(); i++) {
            // �õ������������֤�����֡�
            String temp = verifyCode.substring(i, i+1);

            // �������������ɫ����֤����Ƶ�ͼ���С�
            // ���������ɫ(��Ϊ����ǰ��������ƫ��) 80 //g.setColor(getRandColor(1, 100)); 81
            // ���ú�����������ɫ��ͬ����������Ϊ����̫�ӽ�������ֻ��ֱ������
            g.setColor(new Color(20 + random.nextInt(110), 20 + random
                    .nextInt(110), 20 + random.nextInt(110)));

            g.drawString(temp, 15 * i + 6, 24);
        }
        return buffImg;
    }
}

