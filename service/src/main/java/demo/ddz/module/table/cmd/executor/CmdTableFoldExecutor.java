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

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TableExecutor;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.table.cmd.TableCmdChecker;
import demo.ddz.module.table.cmd.TableCmdExecutor;
import demo.ddz.module.table.cmd.cmd.CmdTableFold;
import demo.ddz.module.table.cmd.result.CmdTableFoldResult;
import demo.ddz.module.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CmdTableFoldExecutor extends TableCmdExecutor<CmdTableFold> {

  private final TableCmdChecker tableCmdChecker;

  private final TableExecutor tableExecutor;

  private final TableRepository tableRepository;

  @Override
  public CmdType support() {
    return CmdType.tableFold;
  }

  @Override
  public void execute(Long tableId, Cmds cmds, CmdTableFold cmd) {
    Table table = tableRepository.find(tableId);
    tableCmdChecker.assertExecution(cmd.getPlayerId(), table, DdzPhaseType.PLAYING);
    TablePlayer player = table.getCurrentPlayer();
    player.disableAuto();
    player.setState(PlayerState.FOLD);
    tableExecutor.execute(table, DdzPhaseType.PLAYING);
    cmds.result(new CmdTableFoldResult(cmd));
  }
}
