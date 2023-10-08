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

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

class WheelTimer {

  @Getter private final long wheelDuration;

  @Getter private final long tickDuration;

  private final HashedWheelTimer timer;

  public WheelTimer(long tickDuration, long ticksPerWheel, HashedWheelTimer timer) {
    this.wheelDuration = tickDuration * ticksPerWheel;
    this.tickDuration = tickDuration;
    this.timer = timer;
  }

  public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
    return timer.newTimeout(task, delay, unit);
  }
}
