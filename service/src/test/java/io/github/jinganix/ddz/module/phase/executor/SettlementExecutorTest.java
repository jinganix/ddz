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
import io.github.jinganix.ddz.module.poker.Card;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("SettlementExecutor")
@ExtendWith(MockitoExtension.class)
class SettlementExecutorTest {

  @InjectMocks SettlementExecutor settlementExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return SETTLEMENT")
      void thenReturn() {
        assertThat(settlementExecutor.getPhaseType()).isEqualTo(DdzPhaseType.SETTLEMENT);
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
      @DisplayName("then return 0")
      void thenReturn() {
        assertThat(settlementExecutor.schedule(new Table())).isEqualTo(0);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        TablePlayer player =
            new TablePlayer().setState(PlayerState.PLAYING).setCards(Collections.emptyList());
        Table table = new Table().setPlayers(List.of(player)).setLandlord(player);

        assertThat(settlementExecutor.execute(table)).isEqualTo(DdzPhaseType.END);
        assertThat(player.getState()).isEqualTo(PlayerState.PLAYING);
      }
    }

    @Nested
    @DisplayName("when landlord win")
    class WhenLandlordWin {

      @Nested
      @DisplayName("when clean sweep")
      class WhenCleanSweep {

        @Test
        @DisplayName("then add fan")
        void thenAddFan() {
          TablePlayer landlord = new TablePlayer().setBidScore(1).setCards(Collections.emptyList());
          TablePlayer player = new TablePlayer();

          Table table =
              new Table()
                  .setPlayers(List.of(landlord, player))
                  .setLandlord(landlord)
                  .setCleanSweep(true);
          settlementExecutor.execute(table);

          assertThat(landlord.getScore()).isEqualTo(2);
          assertThat(player.getScore()).isEqualTo(-2);
        }
      }

      @Nested
      @DisplayName("when not clean sweep")
      class WhenNotCleanSweep {

        @Test
        @DisplayName("then not add fan")
        void thenNotAddFan() {
          TablePlayer landlord = new TablePlayer().setBidScore(1).setCards(Collections.emptyList());
          TablePlayer player = new TablePlayer();

          Table table =
              new Table()
                  .setPlayers(List.of(landlord, player))
                  .setLandlord(landlord)
                  .setCleanSweep(false);
          settlementExecutor.execute(table);

          assertThat(landlord.getScore()).isEqualTo(1);
          assertThat(player.getScore()).isEqualTo(-1);
        }
      }

      @Nested
      @DisplayName("when player doubling")
      class WhenPlayerDoubling {

        @Test
        @DisplayName("then add fan")
        void thenAddFan() {
          TablePlayer landlord = new TablePlayer().setBidScore(1).setCards(Collections.emptyList());
          TablePlayer player = new TablePlayer().setDoubling(true);

          Table table =
              new Table()
                  .setPlayers(List.of(landlord, player))
                  .setLandlord(landlord)
                  .setCleanSweep(false);
          settlementExecutor.execute(table);

          assertThat(landlord.getScore()).isEqualTo(2);
          assertThat(player.getScore()).isEqualTo(-2);
        }
      }

      @Nested
      @DisplayName("when landlord doubling")
      class WhenLandlordDoubling {

        @Test
        @DisplayName("then add fan")
        void thenAddFan() {
          TablePlayer landlord =
              new TablePlayer().setBidScore(1).setCards(Collections.emptyList()).setDoubling(true);
          TablePlayer player = new TablePlayer();

          Table table =
              new Table()
                  .setPlayers(List.of(landlord, player))
                  .setLandlord(landlord)
                  .setCleanSweep(false);
          settlementExecutor.execute(table);

          assertThat(landlord.getScore()).isEqualTo(2);
          assertThat(player.getScore()).isEqualTo(-2);
        }
      }
    }

    @Nested
    @DisplayName("when player win")
    class WhenPlayerWin {

      @Test
      @DisplayName("then settle")
      void thenSettle() {
        TablePlayer landlord = new TablePlayer().setBidScore(1).setCards(List.of(new Card(1)));
        TablePlayer player = new TablePlayer();

        Table table =
            new Table()
                .setPlayers(List.of(landlord, player))
                .setLandlord(landlord)
                .setCleanSweep(false);
        settlementExecutor.execute(table);

        assertThat(landlord.getScore()).isEqualTo(-1);
        assertThat(player.getScore()).isEqualTo(1);
      }
    }
  }
}
