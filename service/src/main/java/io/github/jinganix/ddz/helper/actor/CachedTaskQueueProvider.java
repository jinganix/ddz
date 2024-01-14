package io.github.jinganix.ddz.helper.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.jinganix.peashooter.TaskQueue;
import io.github.jinganix.peashooter.TaskQueueProvider;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CachedTaskQueueProvider implements TaskQueueProvider {

  private final LoadingCache<String, TaskQueue> queues =
      Caffeine.newBuilder()
          .maximumSize(20000)
          .expireAfterAccess(5, TimeUnit.MINUTES)
          .removalListener((key, value, cause) -> log.debug("OrderedTaskQueue is removed: " + key))
          .build(
              key -> {
                log.debug("OrderedTaskQueue is created: " + key);
                return new TaskQueue();
              });

  @Override
  public void remove(String s) {
    this.queues.invalidate(s);
  }

  @Override
  public TaskQueue get(String s) {
    return queues.get(s);
  }
}
