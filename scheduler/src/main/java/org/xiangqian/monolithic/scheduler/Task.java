package org.xiangqian.monolithic.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.xiangqian.monolithic.common.db.sched.entity.TaskEntity;
import org.xiangqian.monolithic.common.db.sched.entity.TaskRecordEntity;
import org.xiangqian.monolithic.common.db.sched.mapper.TaskRecordMapper;

import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 21:57 2024/06/19
 */
@Slf4j
public abstract class Task implements Runnable {

    @Autowired
    private TaskRecordMapper taskRecordMapper;

    private TaskEntity taskEntity;
    private TaskRecordEntity taskRecordEntity;

    public final void setTaskEntity(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
    }

    @Override
    public final void run() {
        try {
            taskRecordEntity = new TaskRecordEntity();
            taskRecordEntity.setTaskId(taskEntity.getId());
            taskRecordEntity.setStartTime(LocalDateTime.now());
            taskRecordMapper.insert(taskRecordEntity);
            execute();
            taskRecordEntity.setEndTime(LocalDateTime.now());
            taskRecordEntity.setStatus((byte) 1);
            taskRecordMapper.updateById(taskRecordEntity);
        } catch (Throwable t) {
            log.error(String.format("任务 id = %s 发生异常", taskEntity.getId(), taskEntity.getName()), t);
            taskRecordEntity.setEndTime(LocalDateTime.now());
            taskRecordEntity.setStatus((byte) 2);
            taskRecordEntity.setMsg(t.getMessage());
            taskRecordMapper.updateById(taskRecordEntity);
        }
    }

    protected abstract void execute() throws Throwable;

}
