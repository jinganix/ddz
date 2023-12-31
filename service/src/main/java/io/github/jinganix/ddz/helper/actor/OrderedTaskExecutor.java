/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
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

package io.github.jinganix.ddz.helper.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.jinganix.ddz.helper.exception.BusinessException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class OrderedTaskExecutor {

  private final LoadingCache<String, OrderedTaskQueue> queues =
      Caffeine.newBuilder()
          .maximumSize(20000)
          .expireAfterAccess(5, TimeUnit.MINUTES)
          .build(key -> new OrderedTaskQueue());

  private final ExecutorService executor;

  public <T> Mono<T> publish(String key, Supplier<Mono<T>> supplier) {
    OrderedTaskQueue queue = queues.get(key);
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

  public void executeSync(Object key, Runnable task) {
    executeSync(String.valueOf(key), task);
  }

  public void executeSync(String key, Runnable task) {
    try {
      OrderedTaskQueue queue = queues.get(key);
      CompletableFuture<Void> future = new CompletableFuture<>();
      Runnable runnable =
          () -> {
            try {
              task.run();
              future.complete(null);
            } catch (Exception ex) {
              future.completeExceptionally(ex);
            }
          };
      queue.execute(getExecutor(), runnable);
      future.get();
    } catch (InterruptedException | ExecutionException ex) {
      if (ex instanceof ExecutionException e && e.getCause() instanceof BusinessException bizEx) {
        throw bizEx;
      }
      throw new RuntimeException(ex.getCause());
    }
  }

  private Executor getExecutor() {
    return Thread.currentThread().isVirtual() ? DirectExecutor.INSTANCE : executor;
  }
}
