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

package io.github.jinganix.ddz.module.phase.executor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CountdownExecutor")
@ExtendWith(MockitoExtension.class)
class CountdownExecutorTest {

  @InjectMocks CountdownExecutor countdownExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return COUNTDOWN")
      void thenReturn() {
        assertThat(countdownExecutor.getPhaseType()).isEqualTo(DdzPhaseType.COUNTDOWN);
      }
    }
  }

  @Nested
  @DisplayName("schedule")
  class Schedule {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return 3000")
      void thenReturn() {
        assertThat(countdownExecutor.schedule(new Table())).isEqualTo(3000);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when ready size is 0")
    class WhenReadySizeIs0 {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        assertThat(countdownExecutor.execute(new Table())).isEqualTo(DdzPhaseType.END);
      }
    }

    @Nested
    @DisplayName("when ready size is 3")
    class WhenReadySizeIs3 {

      @Test
      @DisplayName("then return DEALING")
      void thenReturn() {
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setState(PlayerState.READY),
                        new TablePlayer().setState(PlayerState.READY),
                        new TablePlayer().setState(PlayerState.READY)));

        assertThat(countdownExecutor.execute(table)).isEqualTo(DdzPhaseType.DEALING);
      }
    }
  }
}
