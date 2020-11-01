package com.ofdbox.viewer.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class TaskManage {

    @Value("${task-clear-time}")
    private int taskClearTime;


    private Map<String, Task> tasks = Collections.synchronizedMap(new HashMap<>());
    private ExecutorService service = Executors.newFixedThreadPool(1);

    public void addTask(String id, Task task) {
        tasks.put(id, task);
        service.execute(task);
    }

    public Task getTask(String id) {
        return tasks.get(id);
    }

    @Scheduled(cron = "0 0 * * * ?")
    private void clearTasks() {
        log.info("开始清理任务");
        List<Map.Entry<String, Task>> copy = new ArrayList<>(tasks.entrySet());
        for (Map.Entry<String, Task> entry : copy) {
            if (entry.getValue().getCreateTime() - System.currentTimeMillis() > taskClearTime * 3600 * 1000) {
                tasks.remove(entry.getKey());
                log.info("清理任务："+entry.getKey());
            }
        }
    }
}
