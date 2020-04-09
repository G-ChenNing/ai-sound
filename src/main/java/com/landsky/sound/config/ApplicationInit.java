package com.landsky.sound.config;

import com.landsky.sound.thread.ChiefCustomThreadFactory;
import com.obs.services.ObsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Order(value = 10)

public class ApplicationInit implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationInit.class);
//    private static final ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(2, new ChiefCustomThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    private static ObsClient obsClient;

    @Override
    public void run(String... args) {
        String endPoint = "obs.cn-east-3.myhuaweicloud.com";
        String ak = "DRJUCYEMEIAEP0I3ZQZ8";
        String sk = "MGYq97cUeQY9jBa6P6Nk2uTOdW71ZK9hKmN1i0kE";
// 创建ObsClient实例
        obsClient = new ObsClient(ak, sk, endPoint);
    }

    public static ObsClient getObsClient() {
        return obsClient;
    }

    @PreDestroy
    public void destroy() {
//        if (timer != null && !timer.isShutdown()) {
//            timer.shutdown();
//        }
//        receiver.stop();
//        udpSender.stop();
    }

}
