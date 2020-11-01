package com.ofdbox.viewer.web;

import com.ofdbox.viewer.task.Task;
import com.ofdbox.viewer.task.TaskManage;
import com.ofdbox.viewer.utils.Utils;
import com.ofdbox.viewer.utils.Wapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class ParseController {

    @Value("${store-path}")
    private String storePath;

    @Autowired
    TaskManage taskManage;

    @PostMapping("/upload")
    public Wapper upload(MultipartFile file, HttpServletRequest request){
        try {
            if(!file.getOriginalFilename().endsWith(".ofd")){
                return Wapper.fail(file.getOriginalFilename()+"不是ofd文件");
            }
            String taskId= Utils.uuid()+Utils.uuid();

            File baseDir=new File(new File(storePath), new SimpleDateFormat("yyyyMMdd").format(new Date()) +"/"+taskId);
            FileUtils.forceMkdir(baseDir);
            File ofdFile=new File(baseDir,file.getOriginalFilename());
            InputStream in=file.getInputStream();
            OutputStream out=new FileOutputStream(ofdFile);
            IOUtils.copy(in,out);


            Task task=new Task(taskId,baseDir,ofdFile);
            task.setFilename(file.getOriginalFilename());
            task.setCreateTime(System.currentTimeMillis());
            taskManage.addTask(taskId,task);

            in.close();
            out.close();

            return Wapper.success(taskId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Wapper.fail();
    }


}
