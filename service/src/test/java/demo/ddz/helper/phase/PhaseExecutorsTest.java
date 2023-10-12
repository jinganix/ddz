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

import static demo.ddz.tests.TestConst.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import demo.ddz.helper.actor.OrderedTaskExecutor;
import demo.ddz.helper.timer.MultiLevelWheelTimer;
import demo.ddz.helper.timer.TaskTimer;
import demo.ddz.helper.utils.UtilsService;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PhaseExecutors")
class PhaseExecutorsTest {

  OrderedTaskExecutor orderedTaskExecutor =
      spy(new OrderedTaskExecutor(Executors.newVirtualThreadPerTaskExecutor()));

  UtilsService utilsService = mock(UtilsService.class);

  TaskTimer taskTimer = spy(new MultiLevelWheelTimer(10, 512, 1));

  PhaseExecutors phaseExecutors = new PhaseExecutors(orderedTaskExecutor, utilsService, taskTimer);

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when phase type is null")
    class WhenPhaseTypeIsNull {

      @Test
      @DisplayName("then do nothing")
      void ThenDoNothing() {
        ScheduledPhase phase = mock(ScheduledPhase.class);
        phaseExecutors.execute(phase, null);
        verify(phase, never()).getContext();
      }
    }

    @Nested
    @DisplayName("when in the phase")
    class WhenInThePhase {

      @Test
      @DisplayName("then execute")
      void ThenExecute() {
        ScheduledPhase phase = new ScheduledPhase(mock(PhasedContext.class));
        PhaseType phaseType = mock(PhaseType.class);
        phase.setPhaseType(phaseType);
        PhaseExecutor<?> executor = mock(PhaseExecutor.class);
        when(executor.getPhaseType()).thenReturn(phaseType);

        phaseExecutors.setExecutors(List.of(executor));
        phaseExecutors.execute(phase, phaseType);
        verify(executor, times(1)).execute(any());
      }
    }

    @Nested
    @DisplayName("when in other phase")
    class WhenInOtherPhase {

      @Nested
      @DisplayName("when duration is 0")
      class WhenDurationIs0 {

        @Test
        @DisplayName("then execute")
        void ThenExecute() {
          PhasedContext context = mock(PhasedContext.class);
          when(context.getKey()).thenReturn("KEY");
          ScheduledPhase phase = new ScheduledPhase(context);
          PhaseType phaseType = mock(PhaseType.class);
          PhaseExecutor<?> executor = mock(PhaseExecutor.class);
          when(executor.schedule(any())).thenReturn(0L);
          when(executor.getPhaseType()).thenReturn(phaseType);
          when(utilsService.currentTimeMillis()).thenReturn(MILLIS);

          phaseExecutors.setExecutors(List.of(executor));
          phaseExecutors.execute(phase, phaseType);

          assertThat(phase.getPhaseType()).isEqualTo(phaseType);
          assertThat(phase.getStartAt()).isEqualTo(MILLIS);
          assertThat(phase.getDuration()).isEqualTo(0);
          verify(executor, times(1)).schedule(any());
          verify(executor, times(1)).execute(any());
          verify(taskTimer, never()).schedule(anyLong(), any());
        }
      }

      @Nested
      @DisplayName("when duration is 100")
      class WhenDurationIs1 {
        long duration = 100L;
        PhaseType phaseType;
        PhasedContext phasedContext;
        ScheduledPhase scheduledPhase;
        PhaseExecutor<?> phaseExecutor;

        @BeforeEach
        void setup() {
          phaseType = mock(PhaseType.class);
          phasedContext = mock(PhasedContext.class);
          scheduledPhase = new ScheduledPhase(phasedContext);
          phaseExecutor = mock(PhaseExecutor.class);
          when(phasedContext.getKey()).thenReturn("KEY");
          when(phaseExecutor.schedule(any())).thenReturn(duration);
          when(phaseExecutor.getPhaseType()).thenReturn(phaseType);
          phaseExecutors.setExecutors(List.of(phaseExecutor));
          when(utilsService.currentTimeMillis()).thenReturn(MILLIS);
        }

        @Test
        @DisplayName("then schedule")
        void thenSchedule() {
          phaseExecutors.setExecutors(List.of(phaseExecutor));
          phaseExecutors.execute(scheduledPhase, phaseType);

          assertThat(scheduledPhase.getPhaseType()).isEqualTo(phaseType);
          assertThat(scheduledPhase.getStartAt()).isEqualTo(MILLIS);
          assertThat(scheduledPhase.getDuration()).isEqualTo(duration);
          verify(phaseExecutor, times(1)).schedule(any());
          verify(taskTimer, times(1)).schedule(anyLong(), any());
        }

        @Nested
        @DisplayName("when chained task triggered")
        class WhenChainedTaskTriggered {

          @Test
          @DisplayName("then execute chained task")
          void thenExecuteChainedTask() {
            phaseExecutors.execute(scheduledPhase, phaseType);

            await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(
                    () -> {
                      assertThat(scheduledPhase.getUid()).isEqualTo(2);
                      assertThat(scheduledPhase.getTimer()).isNull();
                    });
          }

          @Nested
          @DisplayName("when phase uid is changed")
          class WhenPhaseUidIsChanged {

            @Test
            @DisplayName("then do not execute phase")
            void thenDoNotExecutePhase() {
              phaseExecutors.execute(scheduledPhase, phaseType);
              scheduledPhase.incrUid();

              await()
                  .atMost(Duration.ofSeconds(1))
                  .untilAsserted(
                      () -> {
                        assertThat(scheduledPhase.getUid()).isEqualTo(2);
                        assertThat(scheduledPhase.getTimer()).isNotNull();
                      });
            }
          }
        }
      }
    }
  }
}
