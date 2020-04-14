package com.landsky.sound.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VoiceDownload {
    /**
     * 根据文件id下载文件
     * @param mediaId
     *            媒体id
     *
     * @throws Exception
     */
    public static InputStream getInputStream(String accessToken, String mediaId) {
        InputStream is = null;
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="
                + accessToken + "&media_id=" + mediaId;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet
                    .openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            // 获取文件转化为byte流
            is = http.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     *
     * 获取下载图片信息（jpg）
     *
     *
     *
     * @param mediaId
     *
     *            文件的id
     *
     * @throws Exception
     */

    public static void saveImageToDisk(String accessToken, String mediaId, String picName, String picPath)
            throws Exception {
        InputStream inputStream = getInputStream(accessToken, mediaId);
        String filePath = picPath+picName+".amr";
        byte[] data = new byte[10240];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            while ((len = inputStream.read(data)) != -1) {
                fileOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //生成对应mp3格式
//            ChangeAudioFormat.changeToMp3(filePath, picPath+picName+".mp3");
        }
    }


    public static void main(String[] args) {
        String token = "32_Qd3AYjBUerC3KZrVrIXxBrEy3zhoeOSDJxHq3e03ih_D091OFR4JGfbWudCKiXmF_HmaipicroQhdcHQ5hQu7Ms-44vUhmQrZb1Gmd8ip1bj4SDZRCiQnKbvNeC7NEUmBUwvNe4gYmooxOjPLHTbAHAQMY";
        String mediaId = "PwjoRS_SxnozJjeFCnglbAqkXtMgNfB3Yk9vmL4OjE9vPVPDQA-Vd6uxmrUKENKA";
        try {
            saveImageToDisk(token,mediaId,"test2.amr","");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
