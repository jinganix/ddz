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

package io.github.jinganix.ddz.module.table.cmd;

import io.github.jinganix.ddz.helper.actor.OrderedTaskExecutor;
import io.github.jinganix.ddz.helper.exception.BusinessException;
import io.github.jinganix.ddz.module.cmd.Cmd;
import io.github.jinganix.ddz.module.cmd.CmdDispatcher;
import io.github.jinganix.ddz.module.cmd.CmdType;
import io.github.jinganix.ddz.module.cmd.Cmds;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.repository.TableRepository;
import io.github.jinganix.ddz.module.utils.ErrorCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableCmdDispatcher implements CmdDispatcher {

  private final Map<CmdType, TableCmdExecutor<Cmd>> executors = new HashMap<>();

  private final OrderedTaskExecutor orderedTaskExecutor;

  private final PlayerRepository playerRepository;

  private final TableRepository tableRepository;

  @Autowired
  @SuppressWarnings("unchecked")
  void setExecutors(List<TableCmdExecutor<? extends Cmd>> executors) {
    for (TableCmdExecutor<? extends Cmd> executor : executors) {
      this.executors.put(executor.support(), (TableCmdExecutor<Cmd>) executor);
    }
  }

  @Override
  public boolean support(CmdType cmdType) {
    return executors.containsKey(cmdType);
  }

  @Override
  public void dispatch(Long playerId, Cmds cmds, Cmd cmd) {
    Player player = playerRepository.find(playerId);
    Long tableId = player.getTableId();
    Table table = tableRepository.find(tableId);
    if (table == null) {
      throw BusinessException.of(ErrorCode.TABLE_NOT_FOUND);
    }
    TableCmdExecutor<Cmd> executor = executors.get(cmd.getCmdType());
    orderedTaskExecutor.executeSync(tableId, () -> executor.execute(tableId, cmds, cmd));
  }
}
