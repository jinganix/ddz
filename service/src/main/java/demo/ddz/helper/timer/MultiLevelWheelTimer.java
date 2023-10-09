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

package demo.ddz.helper.timer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class MultiLevelWheelTimer implements TaskTimer {

  private final List<WheelTimer> timers;

  public MultiLevelWheelTimer(long tickDuration, int ticksPerWheel, int level) {
    WheelTimerThreadFactory threadFactory = new WheelTimerThreadFactory();
    this.timers = new ArrayList<>(level);
    for (int i = 0; i < level; i++) {
      HashedWheelTimer timer =
          new HashedWheelTimer(threadFactory, tickDuration, MILLISECONDS, ticksPerWheel);
      WheelTimer wheelTimer = new WheelTimer(tickDuration, ticksPerWheel, timer);
      timers.add(wheelTimer);
      tickDuration *= ticksPerWheel;
    }
  }

  @Override
  public Timeout schedule(long delay, TimerTask task) {
    if (timers.size() == 1 || delay < timers.get(0).getWheelDuration()) {
      return timers.get(0).newTimeout(task, delay, MILLISECONDS);
    }
    WheelTimer timer = resolve(timers, delay);
    long millis = System.currentTimeMillis();
    DelegatedTimeout delegatedTimeout = new DelegatedTimeout();
    MultiLevelWheelTimerTask timerTask =
        new MultiLevelWheelTimerTask(task, timers, delegatedTimeout, delay, millis);
    long fixedDelay = delay / timer.getTickDuration() * timer.getTickDuration() - 1;
    Timeout timeout = timer.newTimeout(timerTask, fixedDelay, MILLISECONDS);
    delegatedTimeout.setDelegate(timeout);
    return delegatedTimeout;
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
