/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.ddz.helper.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ChainedTaskExecutor {

  private final LoadingCache<String, ChainedTaskQueue> queues =
      Caffeine.newBuilder()
          .maximumSize(20000)
          .expireAfterAccess(5, TimeUnit.MINUTES)
          .build(key -> new ChainedTaskQueue());

  private final ExecutorService executor;

  public ChainedTaskExecutor(ExecutorService executor) {
    this.executor = executor;
  }

  public <T> Mono<T> publish(String key, Supplier<Mono<T>> supplier) {
    ChainedTaskQueue queue = queues.get(key);
    return Mono.create(
        sink ->
            queue.execute(
                getExecutor(),
                () -> {
                  try {
                    T res = supplier.get().block();
                    sink.success(res);
                  } catch (Exception e) {
                    sink.error(e);
                  }
                }));
  }

  public void executeAsync(Object key, Runnable task) {
    executeAsync(String.valueOf(key), task);
  }

  public void executeAsync(String key, Runnable task) {
    queues.get(key).execute(getExecutor(), task);
  }

  private Executor getExecutor() {
    return Thread.currentThread().isVirtual() ? DirectExecutor.INSTANCE : executor;
  }
}
