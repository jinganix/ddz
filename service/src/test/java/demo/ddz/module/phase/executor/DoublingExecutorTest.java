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

package demo.ddz.module.phase.executor;

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DoublingExecutor")
@ExtendWith(MockitoExtension.class)
class DoublingExecutorTest {

  @InjectMocks DoublingExecutor doublingExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return DOUBLING")
      void thenReturn() {
        assertThat(doublingExecutor.getPhaseType()).isEqualTo(DdzPhaseType.DOUBLING);
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
        TablePlayer tablePlayer = new TablePlayer().setState(PlayerState.IDLE);
        assertThat(doublingExecutor.schedule(new Table().setLandlord(tablePlayer))).isEqualTo(3000);
        assertThat(tablePlayer.getState()).isEqualTo(PlayerState.PLAYING);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when current has score")
    class WhenCurrentHasScore {

      @Test
      @DisplayName("then return PLAYING")
      void thenReturn() {
        TablePlayer tablePlayer = new TablePlayer().setState(PlayerState.DOUBLING);
        Table table =
            new Table()
                .setPlayers(List.of(tablePlayer, new TablePlayer().setState(PlayerState.PLAYING)));

        assertThat(doublingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
        assertThat(tablePlayer.getState()).isEqualTo(PlayerState.PLAYING);
        assertThat(tablePlayer.isDoubling()).isFalse();
      }
    }
  }
}
