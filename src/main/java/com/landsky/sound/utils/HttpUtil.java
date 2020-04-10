package com.landsky.sound.utils;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final int CONNECT_TIMEOUT = 30000;// 设置连接建立的超时时间为10s
    protected static final Integer SOCKET_TIMEOUT = 30000;
    private static final int MAX_CONN = 128; // 最大连接数
    private static final int Max_PRE_ROUTE = 128;
    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    private static PoolingHttpClientConnectionManager manager; //连接池管理类
    private static CookieStore cookieStore = new BasicCookieStore();
    private static void setRequestConfig(HttpRequestBase httpRequestBase){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();

        httpRequestBase.setConfig(requestConfig);
    }



    private static IdleConnectionMonitorThread mointor;//监视并回收过期连接
    static {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(100);
        mointor = new IdleConnectionMonitorThread(manager);
        try {
            httpClient = createHttpClient();
            mointor.start();//如果createHttpClient失败 就没必要启动监视失效连接
        }catch (Exception e){
            logger.error("error==>",e);
        }
    }
    /**
     * 根据host和port构建httpclient实例
     * @return
     */
    public static CloseableHttpClient createHttpClient() throws Exception{
        if (httpClient == null) {
            ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
            SSLContext sslContext = (new SSLContextBuilder()).loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
            LayeredConnectionSocketFactory sslSocketFactory =  new SSLConnectionSocketFactory(sslContext, (hostname, session) -> true);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainSocketFactory)
                    .register("https", sslSocketFactory).build();

            manager = new PoolingHttpClientConnectionManager(registry);
            //设置连接参数
            manager.setMaxTotal(MAX_CONN); // 最大连接数
            manager.setDefaultMaxPerRoute(Max_PRE_ROUTE); // 路由最大连接数
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).setDefaultCookieStore(cookieStore).build();
            return client;
        }
        return httpClient;
    }
    public static String doPost(String url, String jsonText){
        HttpPost post = new HttpPost(url);
        String result = "";
        CloseableHttpResponse res = null;
        try {
            if (jsonText != null && !jsonText.isEmpty()) {
                StringEntity entity = new StringEntity(jsonText, ContentType.APPLICATION_JSON);
                post.setEntity(entity);
            }
            setRequestConfig(post);
            res = httpClient.execute(post);
            result = IOUtils.toString(res.getEntity().getContent(), CharacterConstant.UTF_8);
        }catch (Exception e){
            logger.error("error==>{}",e);
        }finally {
            if(null!=res) {
                try {
                    res.close();
                } catch (IOException e) {
                    logger.error("error==>{}",e);
                }
            }
        }
        return result;
    }

    public static String doPostForm(String url, Map<String,String> params){
        HttpPost post = new HttpPost(url);
        String result = "";
        CloseableHttpResponse res = null;
        try {
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            if(params != null && params.size() > 0){
                Set<String> keySet = params.keySet();
                for(String key : keySet) {
                    paramList.add(new BasicNameValuePair(key, params.get(key)));
                }
            }
            post.setEntity(new UrlEncodedFormEntity(paramList,"UTF-8"));
            setRequestConfig(post);
            res = httpClient.execute(post);
            result = IOUtils.toString(res.getEntity().getContent(), CharacterConstant.UTF_8);
        }catch (Exception e){
            logger.error("error==>{}",e);
        }finally {
            if(null!=res) {
                try {
                    res.close();
                } catch (IOException e) {
                    logger.error("error==>{}",e);
                }
            }
        }
        return result;
    }

    public static void wrapCookie(Cookie cookie){
        cookieStore.addCookie(cookie);
    }


    public HttpUtil() {
    }

    public static String doGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        String result = "";
        CloseableHttpResponse res = null;
        try {
            setRequestConfig(get);
            res = httpClient.execute(get);
            result = IOUtils.toString(res.getEntity().getContent(), CharacterConstant.UTF_8);
        } catch (Exception e){
            logger.error("error==>{}",e);
        } finally {
            if (url.startsWith(CharacterConstant.HTTPS) && httpClient != null && httpClient instanceof CloseableHttpClient) {
                httpClient.close();
            }
            res.close();
        }
        return result;
    }
    public static List<Cookie> fetchCookie(String url){
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse res = null;
        try {
            CookieStore cookieStore = new BasicCookieStore();
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            setRequestConfig(get);
            res = httpClient.execute(get);
            return cookieStore.getCookies();
        } catch (Exception e){
            logger.error("error==>{}",e);
        } finally {
            try{
                if (url.startsWith(CharacterConstant.HTTPS) && httpClient != null && httpClient instanceof CloseableHttpClient) {
                    httpClient.close();
                }
                if(null!=res)
                res.close();
            }catch (Exception e){
                logger.error("error==>{}",e);
            }
        }
        return null;
    }



    public static String doGetImage(String url) throws Exception{
        HttpGet get = new HttpGet(url);
        String result = "";
        CloseableHttpResponse res = null;
        try {
            setRequestConfig(get);
            res = httpClient.execute(get);
            HttpEntity entity = res.getEntity();
            byte[] bytes = ImageUtil.readInputStream(entity.getContent());
            result = Base64Util.encode(bytes);
        }catch (Exception e){
            logger.error("error==>{}",e);
        } finally {
           if(null!=res)
           res.close();
        }
        return result;
    }

    public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                logger.error("error==>{}",ex);
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }
}
