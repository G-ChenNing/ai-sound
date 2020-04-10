package com.landsky.sound.utils;

import com.landsky.sound.encode.BASE64Decoder;
import com.landsky.sound.encode.BASE64Encoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * @return
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            logger.error("getImageStr", e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("error==>{}", e);
                }
            }
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
    public static boolean generateImage(String imgStr, String path) {
        OutputStream out = null;
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("error==>{}", e);
                }
            }
        }
    }

    /**
     * 将BufferedImage转换为InputStream
     *
     * @param image
     * @return
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            logger.error("bufferedImageToInputStream", e);
        }
        return null;
    }


    /**
     * 将图片转换成base64格式进行存储
     *
     * @param imagePath
     * @return
     */
    public static String encodeToString(String imagePath) throws IOException {
        String type = StringUtils.substring(imagePath, imagePath.lastIndexOf(".") + 1);
        BufferedImage image = ImageIO.read(new File(imagePath));
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            logger.error("encodeToString", e);
        }
        return imageString;
    }

    /**
     * 将图片转换成base64格式进行存储
     *
     * @param
     * @return
     */
    public static String imageBufferToBase64String(BufferedImage image, String type) throws IOException {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            logger.error("imageBufferToBase64String", e);
        }
        return imageString;
    }

    public static BufferedImage getBufferedImage(String base64string) {
        BufferedImage image = null;
        try {
            InputStream stream = baseToInputStream(base64string);
            image = ImageIO.read(stream);
            //System.out.println(">>>" + image.getWidth() + "," + image.getHeight() + "<<<");
            logger.info(">>>" + image.getWidth() + "," + image.getHeight() + "<<<");
        } catch (IOException e) {
            logger.error("getBufferedImage", e);
        }

        return image;

    }

    public static InputStream baseToInputStream(String base64string) {

        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            logger.error("baseToInputStream", e);
        }
        return stream;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


//    public static void main(String[] args) throws IOException {
//        String s = encodeToString("pic\\1.jpg");
//        byte[] bytes = s.getBytes();
//        String s2 = new String(bytes);
//        generateImage(s2,"pic\\3.jpg");
//    }
}