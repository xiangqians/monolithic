package org.xiangqian.monolithic.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author xiangqian
 * @date 21:00 2024/06/20
 */
@Slf4j
public class ThreadPoolExecutor {

    @Getter
    private java.util.concurrent.ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutor(java.util.concurrent.ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void execute(Runnable task) {
        threadPoolExecutor.execute(task);
    }

    public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return threadPoolExecutor.submit(task, result);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPoolExecutor.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, Duration duration) throws InterruptedException {
        return threadPoolExecutor.invokeAll(tasks, duration.getSeconds(), TimeUnit.SECONDS);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        return threadPoolExecutor.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, Duration duration) throws ExecutionException, InterruptedException, TimeoutException {
        return threadPoolExecutor.invokeAny(tasks, duration.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 在指定时间等待线程池关闭完成
     *
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    public boolean awaitTermination(Duration timeout) throws InterruptedException {
        return threadPoolExecutor.awaitTermination(timeout.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 检查线程池是否已经完全终止
     * 如果线程池已经终止（所有任务都已完成并且已经调用了 shutdown() 方法），则返回 true；否则返回 false。
     * 注意：isTerminated() 方法只会在 shutdown() 方法调用后返回 true。即使线程池中没有活动的任务，如果没有调用 shutdown() 方法，该方法也会返回 false。
     *
     * @return
     */
    public boolean isTerminated() {
        return threadPoolExecutor.isTerminated();
    }

    /**
     * 检查线程池是否正在关闭过程中
     * 如果线程池已经调用了 shutdown() 方法，并且还有一些任务正在执行或者还未开始执行，那么返回 true；否则返回 false。
     * 注意: isTerminating() 方法在调用 shutdown() 方法后，但在所有任务执行完成之前（包括未开始执行的任务），都会返回 true。一旦所有任务都执行完成，并且线程池进入终止状态，isTerminating() 将会返回 false。
     *
     * @return
     */
    public boolean isTerminating() {
        return threadPoolExecutor.isTerminating();
    }

    /**
     * 检查线程池是否已经调用了 shutdown() 方法
     * 如果线程池已经调用了 shutdown() 方法，则返回 true；否则返回 false。
     * 注意：一旦调用了 shutdown() 方法，isShutdown() 方法将会返回 true，即使线程池中还有活动的任务或未开始执行的任务。
     *
     * @return
     */
    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    /**
     * 调用 shutdown() 方法后，线程池将不再接收新的任务，但会继续执行已经提交的任务。在所有任务执行完成后，线程池将真正关闭。
     */
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    /**
     * 调用 shutdownNow() 方法会立即尝试停止所有正在执行的任务，并返回尚未开始执行的任务列表。该方法会发出中断信号给所有线程。如果无法立即停止某些任务，这些任务可能仍然会继续运行。
     *
     * @return
     */
    public List<Runnable> shutdownNow() {
        return threadPoolExecutor.shutdownNow();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ThreadPoolExecutor");
        builder.append("\n\t核心线程数：").append(threadPoolExecutor.getCorePoolSize());
        builder.append("\n\t最大线程数：").append(threadPoolExecutor.getMaximumPoolSize());
        builder.append("\n\t当前正在执行任务的线程数量：").append(threadPoolExecutor.getActiveCount());
        builder.append("\n\t当前线程池中线程数量：").append(threadPoolExecutor.getPoolSize());
        builder.append("\n\t任务队列中等待执行的任务数量：").append(threadPoolExecutor.getQueue().size());
        builder.append("\n\t已经完成执行的任务数量：").append(threadPoolExecutor.getCompletedTaskCount());
        return builder.toString();
    }

    public static ThreadPoolExecutor create() {
        // 获取了CPU核心数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 设置【核心线程数】为：可用处理器数
        int corePoolSize = availableProcessors;
        // 设置【最大线程数】为：可用处理器数 * 2
        int maximumPoolSize = availableProcessors * 2;
        log.debug("ThreadPoolExecutor(corePoolSize={}, maximumPoolSize={})", corePoolSize, maximumPoolSize);

//        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1024);
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue(1024);

        return create(corePoolSize, maximumPoolSize, Duration.ofMinutes(5), workQueue, Executors.defaultThreadFactory(), new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 创建线程池执行器
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   线程空闲时的存活时间
     * @param workQueue       工作队列，用于存储被提交但尚未执行的任务
     * @param threadFactory   创建新线程的工厂
     * @param handler         拒绝策略，用于处理达到最大线程数且任务队列已满时的情况
     * @return
     */
    public static ThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, Duration keepAliveTime, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(new java.util.concurrent.ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime.getSeconds(),
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                handler));
    }

}
