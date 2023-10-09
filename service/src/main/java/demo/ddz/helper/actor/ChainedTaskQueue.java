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

import java.util.LinkedList;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChainedTaskQueue {

  // @protectedby tasks
  private final LinkedList<Task> tasks = new LinkedList<>();

  private final Runnable runner;

  // @protectedby tasks
  private Executor current;

  public ChainedTaskQueue() {
    runner = this::run;
  }

  private void run() {
    for (; ; ) {
      final Task task;
      synchronized (tasks) {
        task = tasks.poll();
        if (task == null) {
          current = null;
          return;
        }
        if (task.exec != current) {
          tasks.addFirst(task);
          task.exec.execute(runner);
          current = task.exec;
          return;
        }
      }
      try {
        task.runnable.run();
      } catch (Throwable t) {
        log.error("Caught unexpected Throwable", t);
      }
    }
  }

  public void execute(Executor executor, Runnable task) {
    synchronized (tasks) {
      tasks.add(new Task(task, executor));
      if (current == null) {
        current = executor;
        executor.execute(runner);
      }
    }
  }

  private static class Task {

    private final Runnable runnable;

    private final Executor exec;

    public Task(Runnable runnable, Executor exec) {
      this.runnable = runnable;
      this.exec = exec;
    }
  }
}
