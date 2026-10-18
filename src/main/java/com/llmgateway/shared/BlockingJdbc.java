package com.llmgateway.shared;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

@Component
public class BlockingJdbc implements DisposableBean {
    public static final int MAX_QUEUED_TASKS = 1000;
    private final Scheduler scheduler;

    // A scheduler is created which is a manager that takes tasks and decides which thread should run them
    public BlockingJdbc(@Value("${spring.datasource.hikari.maximum-pool-size:10}") int poolSize) {
        this.scheduler = Schedulers.newBoundedElastic(poolSize, MAX_QUEUED_TASKS, "jdbc", 60, true);
    }

    // We pass it to callable to freeze execution so we can move it to another thread before it runs
    public <T> Mono<T> call(Callable<T> work) {
        return Mono.fromCallable(work).subscribeOn(scheduler);
    }

    // Executes a runnable non-blockingly
    public Mono<Void> run(Runnable task) {
        return Mono.<Void>fromRunnable(task).subscribeOn(scheduler);
    }

    @Override
    public void destroy() {
        scheduler.dispose();
    }
}
// The computer reads this line and executes it instantly. Because JDBC is blocking, the Event-Loop thread is forced to freeze and stand still for 20ms waiting for the database to respond. Because the Event-Loop is frozen, no other users can connect to your server during those 20ms. The whole server stutters.