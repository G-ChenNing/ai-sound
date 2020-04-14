package com.landsky.sound.utils;

import com.alibaba.fastjson.JSONObject;
import com.landsky.sound.config.ApplicationInit;
import it.sauronsoftware.jave.AudioUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

@Slf4j
public class Token {
    String appid = "wx642bfac3d22ca908";
    String appkey = "d61d9f357e80ef1d85512d06891ebb67";
    String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appkey;

    String fileUtil = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + ApplicationInit.getToken() + "&media_id=";

    public String getToken() {
        String s = null;
        JSONObject jsonObject = null;
        try {
            s = HttpUtil.doGet(token_url);
            jsonObject = JSONObject.parseObject(s);
        } catch (Exception e) {
            return null;
        }
        String access_token = jsonObject.getString("access_token");
        log.error("获得新TOKEN：" + access_token);
        return access_token;
    }

    public void amrToMp3() {
        File source = new File("testAudio.amr");
        File target = new File("testAudio.mp3");
        AudioUtils.amrToMp3(source, target);

    }

    public static void main(String[] args) throws Exception {
//        System.setProperty("ffmpeg.home", "D:\\Program Files\\ffmpeg");
        String testAudio3 = HttpUtil.saveImageToDisk("32_K8SHRewd8cPslh2SMaOCI4gezxR-1J692Uwt_ei2mtQ9ZtD5RfU_327rFD1qQP7BBkibSgv-wMY1qJ93KDht_zjRgoJ-00f1fSP7lQHjJ6_IIgOqUQNy7BAp1ralE2xbcoP7aY8IAK0iKqXQLHDeACAZVR",
                "PwjoRS_SxnozJjeFCnglbAqkXtMgNfB3Yk9vmL4OjE9vPVPDQA-Vd6uxmrUKENKA",
                "testAudio3");


//        String token = "32_Q11ebJcmRFmxX27g7ti2GG3vMEl6hqPkE4RNF6QcXrNJXlLsyB5f0Gfm-ZMzRjQYjDFhXhUPxO_bmOCPRHihxEwioWUwQ19YV1vlip6ePR3Qab57RVUc-onHyaPIYcH6SxuMd1luvFG59ikDSJPjAIAARX";
//        String mediaId = "PwjoRS_SxnozJjeFCnglbAqkXtMgNfB3Yk9vmL4OjE9vPVPDQA-Vd6uxmrUKENKA";
//        String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + mediaId;
//
//
//        URL urlPath = new URL(url);
////            File file = new File("");
////            if (!file.exists()) {
////                file.mkdirs();
////            }
//        FileUtils.copyURLToFile(urlPath, new File("a.amr"));


    }
}
