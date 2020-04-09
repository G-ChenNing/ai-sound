package com.landsky.sound;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.landsky.sound.dao")
public class SoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundApplication.class, args);
    }

}
