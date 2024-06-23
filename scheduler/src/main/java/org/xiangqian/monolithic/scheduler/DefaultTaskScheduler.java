package org.xiangqian.monolithic.scheduler;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author xiangqian
 * @date 21:25 2024/06/20
 */
public class DefaultTaskScheduler {

    private TaskScheduler taskScheduler;

    private Map<Long, ScheduledFuture<?>> scheduledFutureMap;

    public DefaultTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.scheduledFutureMap = new ConcurrentHashMap<>(8, 1f);
    }

    /**
     * 在指定的开始时间只执行一次任务
     *
     * @param task      任务
     * @param startTime 任务的开始时间
     * @return
     */
    public ScheduledFuture<?> schedule(Task task, Instant startTime) {
        return taskScheduler.schedule(task, startTime);
    }

    /**
     * 在指定的开始时间开始执行任务，然后以固定的时间间隔重复执行
     *
     * @param task      任务
     * @param startTime 任务的开始时间
     * @param period    任务执行的时间间隔
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Task task, Instant startTime, Duration period) {
        return taskScheduler.scheduleAtFixedRate(task, startTime, period);
    }

    /**
     * 在指定的开始时间开始执行，然后在每次执行完成后延迟一段固定的时间再执行
     *
     * @param task      任务
     * @param startTime 任务的开始时间
     * @param delay     每次任务执行完成后的延迟时间
     * @return
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Task task, Instant startTime, Duration delay) {
        return taskScheduler.scheduleWithFixedDelay(task, startTime, delay);
    }

    /**
     * 根据触发器（Trigger）规则决定何时执行任务
     * <p>
     * 如果到了任务计划的调度时间，但发现之前的任务尚未完成，通常情况下调度器会等待任务完成后再执行新的调度。
     * 具体来说：
     * 1、任务未完成：如果当前任务还在执行，调度器会等待当前任务执行完毕后再开始新的调度。
     * 2、任务完成：如果任务在调度时间到达时已经执行完毕，调度器会立即开始新的调度。
     * 这种行为确保了任务不会因为调度器的操作而被中断或者重复执行。调度器会在确保任务执行完毕后再进行下一次调度，以保持任务执行的顺序和完整性。
     * 因此，调度器会在任务完成后才会开始新的调度，而不是立即中断当前任务或者同时执行多个任务。
     *
     * @param task    任务
     * @param trigger 任务执行时间的触发器
     * @return
     */
    public ScheduledFuture<?> schedule(Task task, Trigger trigger) {
        return taskScheduler.schedule(task, trigger);
    }

    /**
     * 任务是否已经完成（包括正常完成、取消或由于异常结束）
     *
     * @param scheduledFuture
     * @return
     */
    @Deprecated
    public boolean isDone(ScheduledFuture<?> scheduledFuture) {
        return scheduledFuture.isDone();
    }

    /**
     * 任务是否已被取消
     *
     * @param scheduledFuture
     * @return
     */
    @Deprecated
    public boolean isCancelled(ScheduledFuture<?> scheduledFuture) {
        return scheduledFuture.isCancelled();
    }

    /**
     * 任务是否正在运行中
     * 如果任务既未取消又未完成，则可以认为任务正在运行中
     *
     * @param scheduledFuture
     * @return
     */
    @Deprecated
    public boolean isRunning(ScheduledFuture<?> scheduledFuture) {
        return !isCancelled(scheduledFuture) && !isDone(scheduledFuture);
    }

    /**
     * 任务是否已经停止
     * 如果任务已完成但未取消，则可以认为任务已经停止
     *
     * @param scheduledFuture
     * @return
     */
    @Deprecated
    public boolean isStopped(ScheduledFuture<?> scheduledFuture) {
        return isDone(scheduledFuture) && !isCancelled(scheduledFuture);
    }

    /**
     * 尝试取消对应的定时任务执行
     * 1、如果任务被成功取消，它将不再运行。
     * 2、如果任务已经在执行过程中，那么取决于任务的具体情况：
     * 2.1、如果任务已经开始执行但未完成，那么如果参数设置为 true，则会尝试中断任务的执行，如果参数设置为 false，则任务会继续执行直到完成。
     * 2.2、如果任务已经完成或者从未开始执行，则调用 cancel 方法会返回 false，表示任务并未被取消。
     * 需要注意的是，取消任务并不意味着任务会立刻停止，而是会尽快地中断任务的执行，并使得任务不再能够再次被执行。
     * 另外，如果任务已经开始执行且不支持中断（比如无法响应中断的I/O操作），那么取消操作可能不会立即生效，这时候任务会继续执行直到完成。
     * 总之，scheduledFuture.cancel() 方法是用于取消由 TaskScheduler 安排的定时任务执行，具体的取消行为会取决于任务的执行状态和参数设置。
     *
     * @param scheduledFuture
     * @return
     */
    public boolean cancel(ScheduledFuture<?> scheduledFuture) {
        return scheduledFuture.cancel(true);
    }

}
