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

package io.github.jinganix.ddz.helper.timer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.List;

class MultiLevelWheelTimerTask implements TimerTask {

  private final TimerTask task;

  private final List<WheelTimer> timers;

  private final DelegatedTimeout delegatedTimeout;

  private final long delay;

  private final long startAt;

  public MultiLevelWheelTimerTask(
      TimerTask task,
      List<WheelTimer> timers,
      DelegatedTimeout delegatedTimeout,
      long delay,
      long startAt) {
    this.task = task;
    this.timers = timers;
    this.delegatedTimeout = delegatedTimeout;
    this.delay = delay;
    this.startAt = startAt;
  }

  @Override
  public void run(Timeout timeout) {
    long delay = this.delay - (System.currentTimeMillis() - startAt);
    if (delay <= timers.get(0).getTickDuration()) {
      timers.get(0).newTimeout(task, delay, MILLISECONDS);
      return;
    }
    WheelTimer timer = resolve(timers, delay);
    MultiLevelWheelTimerTask timerTask =
        new MultiLevelWheelTimerTask(task, timers, delegatedTimeout, this.delay, startAt);
    long fixedDelay = delay / timer.getTickDuration() * timer.getTickDuration() - 1;
    delegatedTimeout.setDelegate(timer.newTimeout(timerTask, fixedDelay, MILLISECONDS));
  }

  public static WheelTimer resolve(List<WheelTimer> timers, long delay) {
    WheelTimer timer = null;
    for (WheelTimer wheelTimer : timers) {
      if (delay < wheelTimer.getWheelDuration()) {
        timer = wheelTimer;
        break;
      }
    }
    return timer == null ? timers.get(timers.size() - 1) : timer;
  }
}
