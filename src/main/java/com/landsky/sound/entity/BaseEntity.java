package com.landsky.sound.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;

    public void createStatus() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    public void updateStatus() {
        updateTime = LocalDateTime.now();
    }
}
