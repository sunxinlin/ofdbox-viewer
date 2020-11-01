package com.ofdbox.viewer.web;

import com.ofdbox.viewer.task.Task;
import com.ofdbox.viewer.task.TaskManage;
import com.ofdbox.viewer.utils.Wapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
public class ImageController {

    @Value("${store-path}")
    private String storePath;

    @Autowired
    TaskManage taskManage;

    @GetMapping("/image/{id}/{page}")
    public void getImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String taskId,@PathVariable("page") Integer page){

        Task task=taskManage.getTask(taskId);
        if(task==null){
            response.setStatus(404);
            return;
        }

        File file=new File(task.getBaseDir(),"images/"+page+".png");
        response.setHeader("Content-Disposition", "attachment;Filename="+page+".png");
        try {
            OutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream=new FileInputStream(file);
            IOUtils.copy(inputStream,outputStream);
            inputStream.close();
            outputStream.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setStatus(404);
        return;
    }

    @GetMapping("/task/{id}")
    public Wapper queryTask(HttpServletRequest request, @PathVariable("id") String taskId){

        Task task=taskManage.getTask(taskId);
        if(task!=null){
            return Wapper.success(task);

        }
        return Wapper.fail("任务不存在");
    }

}
