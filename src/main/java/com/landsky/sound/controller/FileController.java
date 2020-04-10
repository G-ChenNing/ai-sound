package com.landsky.sound.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.landsky.sound.config.ApplicationInit;
import com.landsky.sound.entity.OBS;
import com.landsky.sound.entity.ResultWrapper;
import com.landsky.sound.entity.User;
import com.landsky.sound.service.IOBSService;
import com.landsky.sound.service.IUserService;
import com.landsky.sound.service.SendSms;
import com.obs.services.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
//@RequestMapping("/file")
public class FileController {
    private String folder = "D:\\workspace\\sound\\src\\main\\java\\com\\landsky\\sound\\controller";
    public static final String REGEX_MOBILE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
    @Autowired
    private SendSms sendSms;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOBSService iobsService;

    @PostMapping("/file")
    public ResultWrapper upload(MultipartFile file, @RequestParam String openid) throws IOException {
        if (file == null) {
            return ResultWrapper.failure().message("请上传文件");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User one = userService.getOne(queryWrapper);
        if (one.getActived() == 0) {
            return ResultWrapper.failure().message("账号未激活");
        }
        int count = userService.count(queryWrapper);
        if (count > 1000) {
            return ResultWrapper.failure().message("最多1000条");
        }
        String fileName = openid + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        PutObjectResult putObjectResult = ApplicationInit.getObsClient()
                .putObject("obs-for-ai-livius", fileName, new ByteArrayInputStream(file.getBytes()));
        OBS obs = new OBS();
        obs.setBucket_name(putObjectResult.getBucketName());
        obs.setEtag(putObjectResult.getEtag());
        obs.setObjectKey(fileName);
        obs.setOpenid(openid);
        obs.setVersionId(putObjectResult.getVersionId());
        obs.setObjectUrl(putObjectResult.getObjectUrl());
        obs.setCreateTime(LocalDateTime.now());
        iobsService.save(obs);
        return ResultWrapper.success().object(putObjectResult);
    }

    @GetMapping("/list")
    public ResultWrapper upload(@RequestParam String openid) {
        QueryWrapper<OBS> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid).orderByDesc("create_time");
        return ResultWrapper.success().object(iobsService.list(queryWrapper));
    }

    @PostMapping("/register")
    public ResultWrapper register(@RequestParam String phone, @RequestParam String openid) {
        if (phone == null || phone.isEmpty()) {
            return ResultWrapper.failure().message("请输入正确手机号");
        }
        if (openid == null || openid.isEmpty()) {
            return ResultWrapper.failure().message("openid异常");
        }

        if (!phone.matches(REGEX_MOBILE)) {
            return ResultWrapper.failure().message("请输入正确手机号");
        }
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        User user = new User();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User one = userService.getOne(queryWrapper);
        if (one != null && one.getActived() == 1) {
            return ResultWrapper.failure().message("您已注册");
        } else if (one != null && one.getActived() == 0) {
            one.setCode(sRand);
            one.setPhone(phone);
            try {
                sendSms.send(phone, sRand);
            } catch (Exception e) {
                return ResultWrapper.failure().message(e.getMessage());
            }
            userService.updateById(one);
            return ResultWrapper.failure().message("请输入验证码");
        }

        user.setCode(sRand);
        user.setPhone(phone);
        user.setOpenid(openid);
        user.setCreateTime(LocalDateTime.now());
        try {
            sendSms.send(phone, sRand);
            userService.save(user);
        } catch (Exception e) {
            return ResultWrapper.failure().message(e.getMessage());
        }
        return ResultWrapper.success().message("请输入验证码");
    }

    @PostMapping("/active")
    public ResultWrapper active(@RequestParam String openid, @RequestParam String code) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User one = userService.getOne(queryWrapper);
        if (one == null) {
            return ResultWrapper.failure().message("用户不存在");
        }
        if (code.equals(one.getCode())) {
            one.setActived(1);
            one.setUpdateTime(LocalDateTime.now());
            userService.updateById(one);
            return ResultWrapper.success().message("激活成功");
        }
        return ResultWrapper.failure().message("请输入正确验证码");
    }

//    @GetMapping("/{id}")
//    public void download(HttpServletResponse response, @PathVariable("id") String id) {
//        try (InputStream is = new FileInputStream(new File(folder, id + ".txt"))
//             ; OutputStream os = response.getOutputStream();) {
//            response.setContentType("application/x-download");
//            response.addHeader("Content-Disposition", "attachment;filename=test.txt");
//            IOUtils.copy(is, os);
//            os.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
