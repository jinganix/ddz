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

package demo.ddz.module.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.phase.executor.BiddingExecutor;
import demo.ddz.module.phase.executor.CountdownExecutor;
import demo.ddz.module.phase.executor.DealingExecutor;
import demo.ddz.module.phase.executor.DoublingExecutor;
import demo.ddz.module.phase.executor.EndExecutor;
import demo.ddz.module.phase.executor.IdleExecutor;
import demo.ddz.module.phase.executor.PlayingExecutor;
import demo.ddz.module.phase.executor.SettlementExecutor;
import demo.ddz.tests.SpringIntegrationTests;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@DisplayName("TableExecutor")
class TableExecutorTest extends SpringIntegrationTests {

  @SpyBean BiddingExecutor biddingExecutor;

  @SpyBean CountdownExecutor countdownExecutor;

  @SpyBean DealingExecutor dealingExecutor;

  @SpyBean DoublingExecutor doublingExecutor;

  @SpyBean EndExecutor endExecutor;

  @SpyBean IdleExecutor idleExecutor;

  @SpyBean PlayingExecutor playingExecutor;

  @SpyBean SettlementExecutor settlementExecutor;

  @Autowired TableExecutor tableExecutor;

  static class FixedTableCfg extends TableCfg {
    long duration;

    FixedTableCfg(long duration) {
      this.duration = duration;
    }

    @Override
    public long getDuration(DdzPhaseType phaseType) {
      return this.duration;
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when not ready")
    class WhenNotReady {

      @Test
      @DisplayName("then not countdown")
      void thenNotCountdown() {
        Table table =
            new Table()
                .setPlayers(List.of(new TablePlayer(), new TablePlayer(), new TablePlayer()))
                .setCfg(new FixedTableCfg(0));

        tableExecutor.execute(table, DdzPhaseType.IDLE);

        verify(idleExecutor, times(1)).execute(table);
        verify(countdownExecutor, never()).execute(table);
        assertThat(table.getPhase().getPhaseType()).isEqualTo(DdzPhaseType.IDLE);
      }
    }

    @Nested
    @DisplayName("when no bidding")
    class WhenNoBidding {

      @Test
      @DisplayName("then not doubling")
      void thenNotDoubling() {
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setState(PlayerState.READY),
                        new TablePlayer().setState(PlayerState.READY),
                        new TablePlayer().setState(PlayerState.READY)))
                .setCfg(new FixedTableCfg(0));

        tableExecutor.execute(table, DdzPhaseType.IDLE);

        verify(idleExecutor, times(2)).execute(table);
        verify(countdownExecutor, times(1)).execute(table);
        verify(dealingExecutor, times(1)).execute(table);
        verify(biddingExecutor, times(3)).execute(table);
        verify(doublingExecutor, never()).execute(table);
        assertThat(table.getPhase().getPhaseType()).isEqualTo(DdzPhaseType.IDLE);
        assertThat(table.getPlayers()).allMatch(e -> e.getCards() == null);
      }
    }

    @Nested
    @DisplayName("when player bids")
    class WhenPlayerBids {

      @Test
      @DisplayName("then finish execution")
      void thenFinishExecution() {
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setId(1L).setState(PlayerState.DOUBLING).setBidScore(3),
                        new TablePlayer().setId(2L).setState(PlayerState.DOUBLING).setBidScore(2),
                        new TablePlayer().setId(3L).setState(PlayerState.DOUBLING).setBidScore(1)))
                .setCfg(new FixedTableCfg(0));

        dealingExecutor.schedule(table);
        tableExecutor.execute(table, DdzPhaseType.BIDDING);

        verify(idleExecutor, times(1)).execute(table);
        verify(doublingExecutor, times(1)).execute(table);
        verify(playingExecutor, atLeast(1)).execute(table);
        verify(settlementExecutor, times(1)).execute(table);
        verify(endExecutor, times(1)).execute(table);
        assertThat(table.getPhase().getPhaseType()).isEqualTo(DdzPhaseType.IDLE);
        assertThat(table.getPlayers()).allMatch(e -> e.getCards() == null);
        assertThat(table.getPlayers()).allMatch(e -> e.getScore() != 0);
        assertThat(
                table.getPlayers().stream()
                    .map(TablePlayer::getScore)
                    .mapToInt(Integer::intValue)
                    .sum())
            .isEqualTo(0);
      }
    }

    @Nested
    @DisplayName("when run with scheduling")
    class WhenRunWithScheduling {

      @Test
      @DisplayName("then finish execution")
      void thenFinishExecution() {
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setId(1L).setState(PlayerState.DOUBLING).setBidScore(3),
                        new TablePlayer().setId(2L).setState(PlayerState.DOUBLING).setBidScore(2),
                        new TablePlayer().setId(3L).setState(PlayerState.DOUBLING).setBidScore(1)))
                .setCfg(new FixedTableCfg(10));

        dealingExecutor.schedule(table);
        tableExecutor.execute(table, DdzPhaseType.BIDDING);

        await()
            .atMost(Duration.ofSeconds(3))
            .untilAsserted(() -> assertThat(table.getPlayers()).allMatch(e -> e.getScore() != 0));
      }
    }
  }
}
