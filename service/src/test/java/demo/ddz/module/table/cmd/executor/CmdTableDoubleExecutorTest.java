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

package demo.ddz.module.table.cmd.executor;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TableExecutor;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.table.cmd.TableCmdChecker;
import demo.ddz.module.table.cmd.cmd.CmdTableDouble;
import demo.ddz.module.table.cmd.result.CmdTableDoubleResult;
import demo.ddz.module.table.repository.TableRepository;
import demo.ddz.module.utils.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CmdTableDoubleExecutor")
@ExtendWith(MockitoExtension.class)
class CmdTableDoubleExecutorTest {

  @Mock TableCmdChecker tableCmdChecker;

  @Mock TableExecutor tableExecutor;

  @Mock TableRepository tableRepository;

  @InjectMocks CmdTableDoubleExecutor cmdTableDoubleExecutor;

  @Nested
  @DisplayName("support")
  class Support {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(cmdTableDoubleExecutor.support()).isEqualTo(CmdType.tableDouble);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when player is not doubling")
    class WhenPlayerIsNotDoubling {

      @Test
      @DisplayName("then throw INVALID_PLAYER_STATE")
      void thenThrow() {
        TablePlayer player = new TablePlayer().setState(PlayerState.PLAYING);
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTableDouble cmd = new CmdTableDouble(UID_1, true);
        assertThatThrownBy(() -> cmdTableDoubleExecutor.execute(UID_1, cmds, cmd))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.INVALID_PLAYER_STATE);
        verify(tableCmdChecker, times(1)).assertExecution(UID_1, table, DdzPhaseType.DOUBLING);
      }
    }

    @Nested
    @DisplayName("when not all playing")
    class WhenNotAllPlaying {

      @Test
      @DisplayName("then executed")
      void thenExecuted() {
        TablePlayer player1 = new TablePlayer().setState(PlayerState.DOUBLING);
        TablePlayer player2 = new TablePlayer().setState(PlayerState.DOUBLING);
        Table table = new Table().setPlayers(List.of(player1, player2));
        when(tableRepository.find(UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTableDouble cmd = new CmdTableDouble(UID_1, true);
        cmdTableDoubleExecutor.execute(UID_1, cmds, cmd);

        verify(tableCmdChecker, times(1)).assertExecution(UID_1, table, DdzPhaseType.DOUBLING);
        assertThat(player1.isDoubling()).isTrue();
        assertThat(player1.getState()).isEqualTo(PlayerState.PLAYING);

        verify(tableExecutor, never()).execute(table, DdzPhaseType.DOUBLING);
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTableDoubleResult(cmd));
      }
    }

    @Nested
    @DisplayName("when all playing")
    class WhenAllPlaying {

      @Test
      @DisplayName("then executed")
      void ThenExecuted() {
        TablePlayer player1 = new TablePlayer().setState(PlayerState.DOUBLING);
        TablePlayer player2 = new TablePlayer().setState(PlayerState.PLAYING);
        Table table = new Table().setPlayers(List.of(player1, player2));
        when(tableRepository.find(UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTableDouble cmd = new CmdTableDouble(UID_1, true);
        cmdTableDoubleExecutor.execute(UID_1, cmds, cmd);

        verify(tableCmdChecker, times(1)).assertExecution(UID_1, table, DdzPhaseType.DOUBLING);
        assertThat(player1.isDoubling()).isTrue();
        assertThat(player1.getState()).isEqualTo(PlayerState.PLAYING);

        verify(tableExecutor, times(1)).execute(table, DdzPhaseType.DOUBLING);
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTableDoubleResult(cmd));
      }
    }
  }
}
