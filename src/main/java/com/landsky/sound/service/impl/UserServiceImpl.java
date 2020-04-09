package com.landsky.sound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landsky.sound.dao.UserMapper;
import com.landsky.sound.entity.User;
import com.landsky.sound.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wcn
 * @since 2020-03-03
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
