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
import static org.mockito.Mockito.spy;
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
import io.github.jinganix.ddz.module.table.cmd.cmd.CmdTableFold;
import io.github.jinganix.ddz.module.table.cmd.result.CmdTableFoldResult;
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

@DisplayName("CmdTableFoldExecutor")
@ExtendWith(MockitoExtension.class)
class CmdTableFoldExecutorTest {

  @Mock TableCmdChecker tableCmdChecker;

  @Mock TableExecutor tableExecutor;

  @Mock TableRepository tableRepository;

  @InjectMocks CmdTableFoldExecutor cmdTableFoldExecutor;

  @Nested
  @DisplayName("support")
  class Support {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(cmdTableFoldExecutor.support()).isEqualTo(CmdType.tableFold);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when player is playing")
    class WhenPlayerIsPlaying {

      @Test
      @DisplayName("then executed")
      void thenExecuted() {
        TablePlayer player = spy(new TablePlayer().setState(PlayerState.PLAYING));
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTableFold cmd = new CmdTableFold(TestConst.UID_1);
        cmdTableFoldExecutor.execute(TestConst.UID_1, cmds, cmd);

        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
        assertThat(player.getState()).isEqualTo(PlayerState.FOLD);

        verify(player, times(1)).disableAuto();
        verify(tableExecutor, times(1)).execute(table, DdzPhaseType.PLAYING);
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTableFoldResult(cmd));
      }
    }
  }
}
