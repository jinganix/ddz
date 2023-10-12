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

package demo.ddz.module.table.cmd;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import demo.ddz.helper.actor.OrderedTaskExecutor;
import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.player.Player;
import demo.ddz.module.player.PlayerRepository;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.repository.TableRepository;
import demo.ddz.module.utils.ErrorCode;
import demo.ddz.tests.SpringIntegrationTests;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TableCmdDispatcher")
class TableCmdDispatcherTest {

  static class TestArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(CmdType.reserved, false),
          Arguments.of(CmdType.tableBid, true),
          Arguments.of(CmdType.tableDouble, true),
          Arguments.of(CmdType.tableFold, true),
          Arguments.of(CmdType.tablePlay, true));
    }
  }

  @Nested
  @DisplayName("support")
  class Support extends SpringIntegrationTests {

    @Autowired TableCmdDispatcher tableCmdDispatcher;

    @Nested
    @DisplayName("when cmd is provided")
    class WhenCmdIsProvided {

      @ParameterizedTest(name = "support({0}) => {1}")
      @DisplayName("then return if supported")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnIfSupported(CmdType cmdType, boolean expected) {
        assertThat(tableCmdDispatcher.support(cmdType)).isEqualTo(expected);
      }
    }
  }

  @Nested
  @DisplayName("dispatch")
  @ExtendWith(MockitoExtension.class)
  class Dispatch {

    OrderedTaskExecutor orderedTaskExecutor =
        spy(new OrderedTaskExecutor(Executors.newVirtualThreadPerTaskExecutor()));

    @Mock PlayerRepository playerRepository;

    @Mock TableRepository tableRepository;

    @InjectMocks TableCmdDispatcher tableCmdDispatcher;

    @Nested
    @DisplayName("when table not exists")
    class WhenTableNotExists {

      @Test
      @DisplayName("then throw TABLE_NOT_FOUND")
      void thenThrow() {
        Player player = new Player().setTableId(UID_1);
        when(playerRepository.find(UID_1)).thenReturn(player);

        Cmds cmds = new Cmds();
        TableCmd cmd = mock(TableCmd.class);
        assertThatThrownBy(() -> tableCmdDispatcher.dispatch(UID_1, cmds, cmd))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.TABLE_NOT_FOUND);
      }
    }

    @Nested
    @DisplayName("when table exists")
    class WhenTableExists {

      @Test
      @DisplayName("then dispatch")
      void thenDispatch() {
        TableCmdExecutor<TableCmd> executor =
            spy(
                new TableCmdExecutor<TableCmd>() {
                  @Override
                  public CmdType support() {
                    return CmdType.reserved;
                  }

                  @Override
                  public void execute(Long tableId, Cmds cmds, TableCmd cmd) {}
                });
        tableCmdDispatcher.setExecutors(List.of(executor));
        Player player = new Player().setTableId(UID_1);
        when(playerRepository.find(UID_1)).thenReturn(player);
        Table table = new Table();
        when(tableRepository.find(UID_1)).thenReturn(table);
        TableCmd cmd = mock(TableCmd.class);
        when(cmd.getCmdType()).thenReturn(CmdType.reserved);

        Cmds cmds = new Cmds();
        tableCmdDispatcher.dispatch(UID_1, cmds, cmd);

        verify(orderedTaskExecutor, times(1)).executeSync(eq(String.valueOf(UID_1)), any());
        verify(executor, times(1)).execute(UID_1, cmds, cmd);
      }
    }
  }
}
