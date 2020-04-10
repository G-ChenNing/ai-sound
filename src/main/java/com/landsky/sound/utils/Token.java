package com.landsky.sound.utils;

import com.alibaba.fastjson.JSONObject;
import com.landsky.sound.config.ApplicationInit;

public class Token {
    String appid = "wx642bfac3d22ca908";
    String appkey = "d61d9f357e80ef1d85512d06891ebb67";
    String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appkey;

    String fileUtil = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + ApplicationInit.getToken() + "&media_id=";

    public String getToken() {
        String s = null;
        JSONObject jsonObject =null;
        try {
            s = HttpUtil.doGet(token_url);
            jsonObject = JSONObject.parseObject(s);
        } catch (Exception e) {
            return null;
        }
        return jsonObject.getString("access_token");
    }

}
