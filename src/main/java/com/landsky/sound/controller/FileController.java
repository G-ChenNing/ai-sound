package com.landsky.sound.controller;

import com.landsky.sound.config.ApplicationInit;
import com.landsky.sound.entity.FileInfo;
import com.landsky.sound.entity.ResultWrapper;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Random;

@RestController
@RequestMapping("/file")
public class FileController {
    private String folder = "D:\\workspace\\sound\\src\\main\\java\\com\\landsky\\sound\\controller";

    @PostMapping
    public ResultWrapper upload(MultipartFile file) throws IOException {
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        PutObjectResult putObjectResult = ApplicationInit.getObsClient()
                .putObject("obs-for-ai-livius", "livius20200408-01", new ByteArrayInputStream(file.getBytes()));

        return ResultWrapper.success().object(putObjectResult);


    }

    @PostMapping("/register")
    public ResultWrapper register(String phone,String openid) {
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        return ResultWrapper.success().message("请输入验证码");
    }

    @PostMapping("/active")
    public ResultWrapper active(String openid,String code) {
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        return ResultWrapper.success().message("请输入验证码");
    }

    @GetMapping("/{id}")
    public void download(HttpServletResponse response, @PathVariable("id") String id) {
        try (InputStream is = new FileInputStream(new File(folder, id + ".txt"))
             ; OutputStream os = response.getOutputStream();) {
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=test.txt");
            IOUtils.copy(is, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
