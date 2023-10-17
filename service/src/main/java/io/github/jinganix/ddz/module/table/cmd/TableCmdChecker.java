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

import io.github.jinganix.ddz.helper.exception.BusinessException;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import io.github.jinganix.ddz.module.utils.ErrorCode;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class TableCmdChecker {

  public void assertExecution(Long playerId, Table table, DdzPhaseType phaseType) {
    if (table == null) {
      throw BusinessException.of(ErrorCode.TABLE_NOT_FOUND);
    }
    if (table.getPhaseType() != phaseType) {
      throw BusinessException.of(ErrorCode.PHASE_INVALID);
    }
    TablePlayer player = table.getCurrentPlayer();
    if (!Objects.equals(player.getId(), playerId)) {
      throw BusinessException.of(ErrorCode.NOT_CURRENT_PLAYER);
    }
  }
}
