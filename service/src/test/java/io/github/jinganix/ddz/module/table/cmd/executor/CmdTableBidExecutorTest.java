/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
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

package io.github.jinganix.ddz.module.table.cmd.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.module.cmd.CmdType;
import io.github.jinganix.ddz.module.cmd.Cmds;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TableExecutor;
import io.github.jinganix.ddz.module.table.TablePlayer;
import io.github.jinganix.ddz.module.table.cmd.TableCmdChecker;
import io.github.jinganix.ddz.module.table.cmd.cmd.CmdTableBid;
import io.github.jinganix.ddz.module.table.cmd.result.CmdTableBidResult;
import io.github.jinganix.ddz.module.table.repository.TableRepository;
import io.github.jinganix.ddz.tests.TestConst;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CmdTableBidExecutor")
@ExtendWith(MockitoExtension.class)
class CmdTableBidExecutorTest {

  @Mock TableCmdChecker tableCmdChecker;

  @Mock TableExecutor tableExecutor;

  @Mock TableRepository tableRepository;

  @InjectMocks CmdTableBidExecutor cmdTableBidExecutor;

  @Nested
  @DisplayName("support")
  class Support {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(cmdTableBidExecutor.support()).isEqualTo(CmdType.tableBid);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when player is bidding")
    class WhenPlayerIsBidding {

      @Test
      @DisplayName("then executed")
      void ThenExecuted() {
        TablePlayer player = new TablePlayer().setState(PlayerState.BIDDING);
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTableBid cmd = new CmdTableBid(TestConst.UID_1, 1);
        cmdTableBidExecutor.execute(TestConst.UID_1, cmds, cmd);

        assertThat(player.getBidScore()).isEqualTo(1);
        assertThat(player.getState()).isEqualTo(PlayerState.DOUBLING);

        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.BIDDING);
        verify(tableExecutor, times(1)).execute(table, DdzPhaseType.BIDDING);
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTableBidResult(cmd));
      }
    }
  }
}
