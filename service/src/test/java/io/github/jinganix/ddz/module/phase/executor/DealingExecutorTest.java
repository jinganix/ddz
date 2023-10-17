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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.helper.utils.UtilsService;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.poker.CardsSet;
import io.github.jinganix.ddz.module.table.HighestBidder;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DealingExecutor")
@ExtendWith(MockitoExtension.class)
class DealingExecutorTest {

  @Mock UtilsService utilsService;

  @InjectMocks DealingExecutor dealingExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return DEALING")
      void thenReturn() {
        assertThat(dealingExecutor.getPhaseType()).isEqualTo(DdzPhaseType.DEALING);
      }
    }
  }

  @Nested
  @DisplayName("schedule")
  class Schedule {

    @Nested
    @DisplayName("when player auto play")
    class WhenPlayerAutoPlay {

      @Test
      @DisplayName("then deal auto play cards")
      void thenDealAutoPlayCards() {
        TablePlayer player = new TablePlayer().setAuto(2);
        Table table = new Table().setPlayers(List.of(player));

        assertThat(dealingExecutor.schedule(table)).isEqualTo(3000);
        assertThat(player.getCards()).isNull();
        assertThat(player.getAutoPlay().toCards()).hasSize(17).allMatch(Objects::nonNull);
      }
    }

    @Nested
    @DisplayName("when player not auto play")
    class WhenCalled {

      @Test
      @DisplayName("then deal cards")
      void thenCards() {
        TablePlayer player = new TablePlayer().setAuto(1);
        Table table = new Table().setPlayers(List.of(player));

        assertThat(dealingExecutor.schedule(table)).isEqualTo(3000);
        assertThat(player.getCards()).hasSize(17).allMatch(Objects::nonNull);
        assertThat(player.getAutoPlay()).isNull();
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
      @DisplayName("then return BIDDING")
      void thenReturn() {
        when(utilsService.nextInt(anyInt(), anyInt())).thenReturn(1);
        Table table =
            new Table()
                .setHighestBidder(new HighestBidder(UID_1, new CardsSet(Collections.emptyList())))
                .setCursor(2)
                .setPlayers(List.of(new TablePlayer().setState(PlayerState.READY)));

        assertThat(dealingExecutor.execute(table)).isEqualTo(DdzPhaseType.BIDDING);

        assertThat(table.getPlayers())
            .usingRecursiveFieldByFieldElementComparatorOnFields("state")
            .containsExactly(new TablePlayer().setState(PlayerState.BIDDING));
        assertThat(table.getHighestBidder()).isNull();
        assertThat(table.getCursor()).isEqualTo(1);
      }
    }
  }
}
