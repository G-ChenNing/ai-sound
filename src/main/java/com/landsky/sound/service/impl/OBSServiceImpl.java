package com.landsky.sound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landsky.sound.dao.OBSMapper;
import com.landsky.sound.entity.OBS;
import com.landsky.sound.service.IOBSService;
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
public class OBSServiceImpl extends ServiceImpl<OBSMapper, OBS> implements IOBSService {

}
