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

@DisplayName("BiddingExecutor")
@ExtendWith(MockitoExtension.class)
class BiddingExecutorTest {

  @InjectMocks BiddingExecutor biddingExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return BIDDING")
      void thenReturn() {
        assertThat(biddingExecutor.getPhaseType()).isEqualTo(DdzPhaseType.BIDDING);
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
        assertThat(biddingExecutor.schedule(new Table())).isEqualTo(3000);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when landlord is not null")
    class WhenLandlordIsNotNull {

      @Test
      @DisplayName("then return DOUBLING")
      void thenReturn() {
        assertThat(biddingExecutor.execute(new Table().setLandlord(new TablePlayer())))
            .isEqualTo(DdzPhaseType.DOUBLING);
      }
    }

    @Nested
    @DisplayName("when current is bidding")
    class WhenCurrentIsBidding {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        Table table =
            new Table().setPlayers(List.of(new TablePlayer().setState(PlayerState.BIDDING)));

        assertThat(biddingExecutor.execute(table)).isEqualTo(DdzPhaseType.END);

        TablePlayer current = table.getCurrentPlayer();
        assertThat(current.getAuto()).isEqualTo(1);
        assertThat(current.getState()).isEqualTo(PlayerState.DOUBLING);
        assertThat(current.getBidScore()).isEqualTo(0);
      }
    }

    @Nested
    @DisplayName("when players have score")
    class WhenCurrentHasScore {

      @Test
      @DisplayName("then return DOUBLING")
      void thenReturn() {
        TablePlayer landlord = new TablePlayer().setState(PlayerState.DOUBLING).setBidScore(2);
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setState(PlayerState.DOUBLING).setBidScore(1), landlord));

        assertThat(biddingExecutor.execute(table)).isEqualTo(DdzPhaseType.DOUBLING);

        assertThat(landlord.getAuto()).isEqualTo(0);
        assertThat(landlord.getState()).isEqualTo(PlayerState.DOUBLING);
        assertThat(landlord.getBidScore()).isEqualTo(2);

        assertThat(table.getLandlord()).isEqualTo(landlord);
        assertThat(table.getCursor()).isEqualTo(1);
      }
    }

    @Nested
    @DisplayName("when any player in bidding")
    class WhenAnyPlayerInBidding {

      @Test
      @DisplayName("then return BIDDING")
      void thenReturn() {
        Table table =
            new Table()
                .setPlayers(
                    List.of(
                        new TablePlayer().setState(PlayerState.DOUBLING),
                        new TablePlayer().setState(PlayerState.BIDDING)));

        assertThat(biddingExecutor.execute(table)).isEqualTo(DdzPhaseType.BIDDING);
        assertThat(table.getCursor()).isEqualTo(1);
      }
    }
  }
}
