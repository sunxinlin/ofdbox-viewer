package com.ofdbox.viewer.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ofdbox.convertor.img.Ofd2Img;
import com.ofdbox.core.*;
import com.ofdbox.core.model.OFD;
import com.ofdbox.core.model.page.Page;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Task implements Runnable {

    @JsonIgnore
    private String taskId;
    @JsonIgnore
    private File baseDir;
    @JsonIgnore
    private File file;

    private State state = State.WAITING;
    private long createTime;
    private String filename;
    private int currentPage;
    private List<Integer> pages = new ArrayList<>();

    private static OFDReader ofdReader = null;
    private static Ofd2Img ofd2Img = new Ofd2Img();

    static {
        ofdReader = new OFDReader();
        ofdReader.getConfig().setValid(false);
        ofd2Img = new Ofd2Img();
//        ofd2Img.getConfig().setDrawBoundary(true);
    }


    public Task(String id, File baseDir, File file) {
        this.taskId = id;
        this.baseDir = baseDir;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            state = State.PARSING;
            OFD ofd = ofdReader.read(file);
            File imageDie = new File(baseDir, "images");
            imageDie.mkdirs();
            state = State.RENDERING;
            for (Page page : ofd.getDocuments().get(0).getPages()) {
                currentPage++;
                File file = new File(imageDie, currentPage + ".png");
                BufferedImage image = ofd2Img.toImage(page,20);
                ImageIO.write(image, "PNG", file);
                pages.add(currentPage);
            }
            state = State.COMPLETED;
        } catch (IOException e) {
            e.printStackTrace();
            state = State.ERROR;
        }
    }
}
