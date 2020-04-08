package com.landsky.sound.controller;

import com.landsky.sound.entity.FileInfo;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

@RestController
@RequestMapping("/file")
public class FileController {
    private String folder = "D:\\workspace\\sound\\src\\main\\java\\com\\landsky\\sound\\controller";

    @PostMapping
    public FileInfo upload(MultipartFile file) throws IOException {
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        File localFile = new File(folder, System.currentTimeMillis() + ".txt");
        file.transferTo(localFile);

        String endPoint = "obs.cn-east-3.myhuaweicloud.com";
        String ak = "DRJUCYEMEIAEP0I3ZQZ8";
        String sk = "MGYq97cUeQY9jBa6P6Nk2uTOdW71ZK9hKmN1i0kE";
// 创建ObsClient实例
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);

//        String content = "Hello OBS";
        PutObjectResult putObjectResult = obsClient.putObject("obs-for-ai-livius", "livius20200408-01", new ByteArrayInputStream(file.getBytes()));


        return new FileInfo(localFile.getAbsolutePath());

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
