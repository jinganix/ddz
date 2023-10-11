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

package demo.ddz.helper.phase;

import io.netty.util.Timeout;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScheduledPhase {

  @Setter(AccessLevel.NONE)
  private long uid;

  @Setter(AccessLevel.NONE)
  private PhasedContext context;

  private PhaseType phaseType;

  private long startAt;

  private long duration;

  private Timeout timer;

  public ScheduledPhase(PhasedContext context) {
    this.context = context;
  }

  public void incrUid() {
    this.uid++;
  }

  public void cancelTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}
