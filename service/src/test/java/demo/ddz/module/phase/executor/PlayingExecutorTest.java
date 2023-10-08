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
import demo.ddz.module.poker.Card;
import demo.ddz.module.table.HighestBidder;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
import java.util.Collections;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PlayingExecutor")
@ExtendWith(MockitoExtension.class)
class PlayingExecutorTest {

  @InjectMocks PlayingExecutor playingExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return PLAYING")
      void thenReturn() {
        assertThat(playingExecutor.getPhaseType()).isEqualTo(DdzPhaseType.PLAYING);
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
      @DisplayName("then return 15000")
      void thenReturn() {
        TablePlayer player = new TablePlayer().setId(1L).setState(PlayerState.FOLD);
        HighestBidder bidder = new HighestBidder(2L, Collections.emptyList());
        Table table = new Table().setPlayers(List.of(player)).setHighestBidder(bidder);

        assertThat(playingExecutor.schedule(table)).isEqualTo(15000);
        assertThat(table.getHighestBidder()).isEqualTo(bidder);
        assertThat(player.getState()).isEqualTo(PlayerState.FOLD);
      }
    }

    @Nested
    @DisplayName("when current is highest bidder")
    class WhenCurrentIsHighestBidder {

      @Test
      @DisplayName("then reset highest bidder")
      void thenReset() {
        TablePlayer player = new TablePlayer().setId(1L);
        Table table =
            new Table()
                .setPlayers(List.of(player))
                .setHighestBidder(new HighestBidder(1L, Collections.emptyList()));

        assertThat(playingExecutor.schedule(table)).isEqualTo(15000);
        assertThat(table.getHighestBidder()).isNull();
        assertThat(player.getState()).isEqualTo(PlayerState.PLAYING);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when current fold")
    class WhenCurrentFold {

      @Test
      @DisplayName("then return PLAYING")
      void thenReturn() {
        TablePlayer player = new TablePlayer().setState(PlayerState.FOLD);
        Table table = new Table().setPlayers(List.of(player));

        assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
      }
    }

    @Nested
    @DisplayName("when highest bidder is null")
    class WhenHighestBidderIsNull {

      @Nested
      @DisplayName("when auto play is not null")
      class WhenAutoPlayIsNotNull {

        @Test
        @DisplayName("then auto play")
        void thenAutoPlay() {
          TablePlayer player =
              new TablePlayer()
                  .setState(PlayerState.PLAYING)
                  .setAuto(1)
                  .setCards(List.of(new Card(1)));

          Table table = new Table().setPlayers(List.of(player));
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
        }
      }

      @Nested
      @DisplayName("when auto play is null")
      class WhenAutoPlayIsNull {

        @Test
        @DisplayName("then fold")
        void thenFold() {
          TablePlayer player =
              new TablePlayer().setState(PlayerState.PLAYING).setCards(List.of(new Card(1)));

          Table table = new Table().setPlayers(List.of(player));
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
          assertThat(player.getState()).isEqualTo(PlayerState.FOLD);
        }
      }
    }

    @Nested
    @DisplayName("when current is not highest bidder")
    class WhenCurrentIsNotHighestBidder {

      @Nested
      @DisplayName("when auto play is not null")
      class WhenAutoPlayIsNotNull {

        @Test
        @DisplayName("then auto play")
        void thenAutoPlay() {
          TablePlayer player =
              new TablePlayer()
                  .setState(PlayerState.PLAYING)
                  .setAuto(1)
                  .setCards(List.of(new Card(1)));

          Table table =
              new Table()
                  .setPlayers(List.of(player))
                  .setHighestBidder(new HighestBidder(2L, Lists.newArrayList(new Card(2))));
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
        }
      }
    }

    @Nested
    @DisplayName("when cards is empty")
    class WhenCardsIsEmpty {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        HighestBidder bidder = new HighestBidder(1L, Collections.emptyList());
        TablePlayer player =
            new TablePlayer()
                .setId(1L)
                .setState(PlayerState.PLAYING)
                .setCards(Collections.emptyList());
        Table table = new Table().setPlayers(List.of(player)).setHighestBidder(bidder);

        assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.END);
      }
    }

    @Nested
    @DisplayName("when cards is not empty")
    class WhenCardsIsNotEmpty {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        HighestBidder bidder = new HighestBidder(1L, Collections.emptyList());
        TablePlayer player =
            new TablePlayer()
                .setId(1L)
                .setState(PlayerState.PLAYING)
                .setCards(List.of(new Card(1)));
        Table table =
            new Table().setPlayers(List.of(player, new TablePlayer())).setHighestBidder(bidder);

        assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
        assertThat(table.getCursor()).isEqualTo(1);
      }
    }
  }
}
