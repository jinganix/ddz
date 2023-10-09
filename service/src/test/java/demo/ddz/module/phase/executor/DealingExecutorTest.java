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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import demo.ddz.helper.utils.UtilsService;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.poker.CardsSet;
import demo.ddz.module.table.HighestBidder;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
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
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return 3000")
      void thenReturn() {
        TablePlayer player = new TablePlayer();
        Table table = new Table().setPlayers(List.of(player));

        assertThat(dealingExecutor.schedule(table)).isEqualTo(3000);
        assertThat(player.getCards()).hasSize(17).allMatch(Objects::nonNull);
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
                .setHighestBidder(new HighestBidder(1L, new CardsSet(Collections.emptyList())))
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
