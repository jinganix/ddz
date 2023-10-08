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

package demo.ddz.helper.phase;

import demo.ddz.helper.actor.ChainedTaskExecutor;
import demo.ddz.helper.timer.TaskTimer;
import demo.ddz.helper.utils.UtilsService;
import io.netty.util.Timeout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhaseExecutors {

  private final Map<PhaseType, PhaseExecutor<PhasedContext>> executors = new HashMap<>();

  private final ChainedTaskExecutor chainedTaskExecutor;

  private final UtilsService utilsService;

  private final TaskTimer taskTimer;

  @Autowired
  @SuppressWarnings("unchecked")
  void setExecutors(List<PhaseExecutor<?>> executors) {
    for (PhaseExecutor<?> phase : executors) {
      this.executors.put(phase.getPhaseType(), (PhaseExecutor<PhasedContext>) phase);
    }
  }

  public void execute(ScheduledPhase phase, PhaseType phaseType) {
    phase.cancelTimer();
    phase.incrUid();
    while (phaseType != null) {
      PhasedContext context = phase.getContext();
      PhaseExecutor<PhasedContext> executor = executors.get(phaseType);
      if (phase.getPhaseType() == phaseType) {
        phaseType = executor.execute(context);
        continue;
      }
      long millis = utilsService.currentTimeMillis();
      phase.setPhaseType(phaseType);
      phase.setStartAt(millis);
      phase.setDuration(executor.schedule(context));
      if (phase.getDuration() <= 0) {
        phaseType = executor.execute(context);
        continue;
      }
      schedule(phase, phaseType);
      break;
    }
  }

  private void schedule(ScheduledPhase phase, PhaseType phaseType) {
    long uid = phase.getUid();
    phase.cancelTimer();
    Timeout timer =
        taskTimer.schedule(
            phase.getDuration(),
            e ->
                chainedTaskExecutor.executeAsync(
                    phase.getContext().getKey(),
                    () -> {
                      if (uid == phase.getUid()) {
                        phase.setTimer(null);
                        this.execute(phase, phaseType);
                      }
                    }));
    phase.setTimer(timer);
  }
}
