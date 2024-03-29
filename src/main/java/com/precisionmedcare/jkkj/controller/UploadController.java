package com.precisionmedcare.jkkj.controller;

import com.precisionmedcare.jkkj.model.TestEntity;
import com.precisionmedcare.jkkj.service.UploadService;
import com.precisionmedcare.jkkj.utils.GetAddressIP;
import com.precisionmedcare.jkkj.utils.Result;
import com.precisionmedcare.jkkj.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;
    private final static String IMAGE_PATH = "static/upload/image/";
    @RequestMapping("image")
    public Result upload(HttpServletRequest request, HttpSession session) {
        Result msg = new Result();
        MultipartHttpServletRequest Murequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> files = Murequest.getFileMap();//得到文件map对象
        for(MultipartFile file :files.values()){

            if (file.isEmpty()) {
                // 设置错误状态码
                msg.setSuccess(false);
                msg.setContent("上传失败，请选择文件");
                return msg;
            }
            // 拿到文件名
            String filename = file.getOriginalFilename();
            // 存放上传图片的文件夹
            File fileDir = UploadUtils.getImgDirFile(session);
            // 输出文件夹绝对路径  -- 这里的绝对路径是相当于当前项目的路径而不是“容器”路径
//            System.out.println(fileDir.getAbsolutePath());
            try {
                // 构建真实的文件路径
                File newFile = new File(fileDir.getAbsolutePath() + File.separator + filename);
//                System.out.println(newFile.getAbsolutePath());

                // 上传图片到 -》 “绝对路径”
                file.transferTo(newFile);
                msg.setSuccess(true);
                msg.setContent(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    @RequestMapping(value = "insertResultGroup", method = RequestMethod.POST)
    public Result insertResultGroup(HttpServletRequest request, @RequestBody Map model,HttpSession session) {
        Result result = new Result();
        String ipAddress = GetAddressIP.getClientIpAddress(request);
        try {
            uploadService.insertResultGroup(model,ipAddress,session,IMAGE_PATH);
            /*
            *   执行成功执行udp编程调用python接口
            * */
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }
        return result;
    }
}
