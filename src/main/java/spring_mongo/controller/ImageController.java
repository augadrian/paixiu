package spring_mongo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import spring_mongo.util.UploadImage;
import spring_mongo.util.UploadResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Created by Yagamic on 2017/1/9.
 */
@Controller
@RequestMapping(value = "/api/imageManager", produces = "application/json;charset=UTF-8")
public class ImageController {
    ObjectMapper mapToJson = new ObjectMapper();

    //编辑器上传图片
    @RequestMapping(value = {"/uploadImage"})
    @ResponseBody
    public String uploadImage(MultipartFile myFileName, HttpServletRequest request) throws Exception {
        String uploadPath = "others";
        UploadResult uploadResult = UploadImage.uploadImage(myFileName, uploadPath, request);
        String host = "http://" + request.getServerName() + ":" + request.getServerPort() + "/";
        String url = host + uploadResult.getFinalFileName();
        System.out.println(url);
        return url;
    }

    //上传图片
    @RequestMapping(value = {"/uploadFile"})
    @ResponseBody
    public String uploadFile(MultipartFile file, String uploadPath, HttpServletRequest request) throws IOException {
        UploadResult uploadResult = UploadImage.uploadImage(file, uploadPath, request);
        return mapToJson.writeValueAsString(uploadResult);
    }
}

