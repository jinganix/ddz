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

import static io.github.jinganix.ddz.tests.TestConst.UID_1;
import static io.github.jinganix.ddz.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.poker.Card;
import io.github.jinganix.ddz.module.poker.CardsSet;
import io.github.jinganix.ddz.module.table.HighestBidder;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
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
    @DisplayName("when current is fold")
    class WhenCurrentIsFold {

      @Test
      @DisplayName("then return 0")
      void thenReturn() {
        TablePlayer player = new TablePlayer().setId(UID_1).setState(PlayerState.FOLD);
        Table table = new Table().setPlayers(List.of(player));

        assertThat(playingExecutor.schedule(table)).isEqualTo(0);
      }
    }

    @Nested
    @DisplayName("when current is not fold")
    class WhenCurrentIsNotFold {

      @Test
      @DisplayName("then return 15000")
      void thenReturn() {
        TablePlayer player = new TablePlayer().setId(UID_1).setState(PlayerState.PLAYING);
        HighestBidder bidder = new HighestBidder(UID_2, new CardsSet(emptyList()));
        Table table = new Table().setPlayers(List.of(player)).setHighestBidder(bidder);

        assertThat(playingExecutor.schedule(table)).isEqualTo(15000);
        assertThat(table.getHighestBidder()).isEqualTo(bidder);
        assertThat(player.getState()).isEqualTo(PlayerState.PLAYING);
      }
    }

    @Nested
    @DisplayName("when current is highest bidder")
    class WhenCurrentIsHighestBidder {

      @Test
      @DisplayName("then reset highest bidder")
      void thenReset() {
        TablePlayer player = new TablePlayer().setId(UID_1);
        Table table =
            new Table()
                .setPlayers(List.of(player))
                .setHighestBidder(new HighestBidder(UID_1, new CardsSet(emptyList())));

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
        Table table = new Table().setPlayers(List.of(player, new TablePlayer()));

        assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
        assertThat(table.getCursor()).isEqualTo(1);
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

          Table table = new Table().setPlayers(List.of(player)).setLandlord(player);
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.SETTLEMENT);
        }
      }

      @Nested
      @DisplayName("when auto play is null")
      class WhenAutoPlayIsNull {

        @Test
        @DisplayName("then enable auto play")
        void thenEnableAutoPlay() {
          TablePlayer player =
              new TablePlayer().setState(PlayerState.PLAYING).setCards(List.of(new Card(1)));

          Table table = new Table().setPlayers(List.of(player)).setLandlord(player);
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.SETTLEMENT);
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
                  .setLandlord(player)
                  .setHighestBidder(
                      new HighestBidder(UID_2, new CardsSet(Lists.newArrayList(new Card(2)))));
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

          Table table =
              new Table()
                  .setPlayers(List.of(player))
                  .setLandlord(player)
                  .setHighestBidder(
                      new HighestBidder(UID_2, new CardsSet(Lists.newArrayList(new Card(2)))));
          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
          assertThat(player.getState()).isEqualTo(PlayerState.FOLD);
        }
      }
    }

    @Nested
    @DisplayName("when current is highest bidder")
    class WhenCurrentIsHighestBidder {

      @Nested
      @DisplayName("when highest bidder is bomb")
      class WhenHighestBidderIsBomb {

        @Test
        @DisplayName("then add bomb count")
        void thenAddBombCount() {
          HighestBidder bidder =
              new HighestBidder(
                  UID_1,
                  new CardsSet(
                      Lists.newArrayList(new Card(1), new Card(14), new Card(27), new Card(40))));
          TablePlayer player =
              new TablePlayer().setId(UID_1).setState(PlayerState.PLAYING).setCards(emptyList());
          Table table =
              new Table().setPlayers(List.of(player)).setLandlord(player).setHighestBidder(bidder);

          playingExecutor.execute(table);

          assertThat(table.getBombCount()).isEqualTo(1);
        }
      }

      @Nested
      @DisplayName("when highest bidder is rocket")
      class WhenHighestBidderIsRocket {

        @Test
        @DisplayName("then add bomb count")
        void thenAddBombCount() {
          HighestBidder bidder =
              new HighestBidder(
                  UID_1, new CardsSet(Lists.newArrayList(new Card(53), new Card(54))));
          TablePlayer player =
              new TablePlayer().setId(UID_1).setState(PlayerState.PLAYING).setCards(emptyList());
          Table table =
              new Table().setPlayers(List.of(player)).setLandlord(player).setHighestBidder(bidder);

          playingExecutor.execute(table);

          assertThat(table.getBombCount()).isEqualTo(1);
        }
      }

      @Nested
      @DisplayName("when player cards is empty")
      class WhenPlayerCardsIsEmpty {

        @Test
        @DisplayName("then return SETTLEMENT")
        void thenReturn() {
          HighestBidder bidder = new HighestBidder(UID_1, new CardsSet(emptyList()));
          TablePlayer player =
              new TablePlayer().setId(UID_1).setState(PlayerState.PLAYING).setCards(emptyList());
          Table table =
              new Table().setPlayers(List.of(player)).setLandlord(player).setHighestBidder(bidder);

          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.SETTLEMENT);
        }
      }

      @Nested
      @DisplayName("when player cards is not empty")
      class WhenPlayerCardsIsNotEmpty {

        @Test
        @DisplayName("then return PLAYING")
        void thenReturn() {
          HighestBidder bidder = new HighestBidder(UID_1, new CardsSet(emptyList()));
          TablePlayer player =
              new TablePlayer()
                  .setId(UID_1)
                  .setState(PlayerState.PLAYING)
                  .setCards(List.of(new Card(1)));
          Table table =
              new Table()
                  .setPlayers(List.of(player, new TablePlayer()))
                  .setLandlord(player)
                  .setHighestBidder(bidder);

          assertThat(playingExecutor.execute(table)).isEqualTo(DdzPhaseType.PLAYING);
          assertThat(table.getCursor()).isEqualTo(1);
        }
      }
    }

    @Nested
    @DisplayName("when highest bidder is not landlord")
    class WhenHighestBidderIsNotLandlord {

      @Test
      @DisplayName("then not clean sweep")
      void thenNotCleanSweep() {
        HighestBidder bidder = new HighestBidder(UID_1, new CardsSet(emptyList()));
        TablePlayer player = new TablePlayer().setId(UID_2).setCards(emptyList());
        Table table =
            new Table().setPlayers(List.of(player)).setLandlord(player).setHighestBidder(bidder);

        playingExecutor.execute(table);

        assertThat(table.isCleanSweep()).isFalse();
      }
    }
  }
}
