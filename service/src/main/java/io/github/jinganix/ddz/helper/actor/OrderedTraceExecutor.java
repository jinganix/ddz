package io.github.jinganix.ddz.helper.actor;

import io.github.jinganix.peashooter.DefaultExecutorSelector;
import io.github.jinganix.peashooter.OrderedTraceRunnable;
import io.github.jinganix.peashooter.TaskQueue;
import io.github.jinganix.peashooter.TaskQueueProvider;
import io.github.jinganix.peashooter.TraceExecutor;
import io.github.jinganix.peashooter.TraceRunnable;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OrderedTraceExecutor extends io.github.jinganix.peashooter.OrderedTraceExecutor {

  public OrderedTraceExecutor(TaskQueueProvider queueProvider, TraceExecutor executor) {
    super(queueProvider, new DefaultExecutorSelector(executor), executor.getTracer());
  }

  public <T> Mono<T> publish(String key, Supplier<Mono<T>> supplier) {
    TaskQueue queue = queues.get(key);
    return Mono.create(
        sink -> {
          TraceRunnable runnable =
              new OrderedTraceRunnable(
                  tracer,
                  key,
                  true,
                  () -> {
                    try {
                      T res = supplier.get().block();
                      sink.success(res);
                    } catch (Exception e) {
                      sink.error(e);
                    }
                  });
          queue.execute(selector.getExecutor(queue, runnable, true), runnable);
        });
  }

  public <R> R supply(Object key, Supplier<R> supplier) {
    return supply(String.valueOf(key), supplier);
  }

  public void executeAsync(Object key, Runnable task) {
    executeAsync(String.valueOf(key), task);
  }

  public void executeSync(Object key, Runnable task) {
    executeSync(String.valueOf(key), task);
  }
}
