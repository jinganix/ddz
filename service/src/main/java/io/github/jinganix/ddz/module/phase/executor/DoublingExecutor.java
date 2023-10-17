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

package io.github.jinganix.ddz.module.phase.executor;

import io.github.jinganix.ddz.helper.phase.PhaseExecutor;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoublingExecutor extends PhaseExecutor<Table> {

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.DOUBLING;
  }

  @Override
  public long schedule(Table table) {
    table.getLandlord().setState(PlayerState.PLAYING);
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    for (TablePlayer player : table.getPlayers()) {
      if (player.isState(PlayerState.DOUBLING)) {
        player.setState(PlayerState.PLAYING);
        player.setDoubling(false);
      }
    }
    return DdzPhaseType.PLAYING;
  }
}
