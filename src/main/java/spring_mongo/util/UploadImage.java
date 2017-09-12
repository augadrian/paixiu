package spring_mongo.util;


import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上传图片接口
 */
public class UploadImage {


    public static UploadResult uploadImage(MultipartFile file, String uploadPath, HttpServletRequest request) throws IOException {
        UploadResult uploadResult = null;
        String realpath = request.getSession().getServletContext().getRealPath("");//获取服务器路径
        Date nowTime = new Date(System.currentTimeMillis());
        //当前日期
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);


        if (file != null) {// 判断上传的文件是否为空
            // 图片大小限制
            if (file.getSize() > 5 * 1024 * 1024) {
                uploadResult = new UploadResult(500, "文件大小超出范围");
            } else {
                String path = null;// 文件路径
                String type = null;// 文件类型
                String fileName = file.getOriginalFilename();// 文件原名称
                System.out.println("上传的文件原名称:" + fileName);
                // 判断文件类型
                type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
                if (type != null) {// 判断文件类型是否为空
                    if ("GIF".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase()) || "JPG".equals(type.toUpperCase())) {
                        // 项目在容器中实际发布运行的根路径
                        String realPath = request.getSession().getServletContext().getRealPath("/");
                        // 自定义的文件名称
                        SimpleDateFormat sdFormatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
                        String fileNameDate = sdFormatter1.format(nowTime);
                        String[] fileName1 = fileName.split("\\.");
                        String trueFileName = retStrFormatNowDate + "/" + fileNameDate + "." + fileName1[1];//上传后的头像真实的路径+名称
                        // 设置存放图片文件的路径
                        path = realPath + "/" + selectPath(uploadPath) + trueFileName;
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        System.out.println("存放图片文件的路径:" + path);
                        // 转存文件到指定的路径
                        file.transferTo(new File(path));
                        uploadResult = new UploadResult(200, "上传成功", selectPath(uploadPath) + trueFileName);
                    } else {
                        uploadResult = new UploadResult(500, "不是我们想要的文件类型,请按要求重新上传");
                    }
                } else {
                    uploadResult = new UploadResult(500, "文件类型为空");
                }
            }
        } else {
            uploadResult = new UploadResult(500, "没有找到相对应的文件");
        }
        return uploadResult;
    }

    public static String selectPath(String uploadPath) {
        String path = null;
        switch (uploadPath) {
            case "user":
                path = "upload/user/";
                break;
            case "news":
                path = "upload/news/";
                break;
            case "instrument":
                path = "upload/instrument/";
                break;
            default:
                path = "upload/others/";
                break;
        }
        return path;
    }
}
