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
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementExecutor extends PhaseExecutor<Table> {

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.SETTLEMENT;
  }

  @Override
  public long schedule(Table table) {
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    int tableFan = table.getBombCount() + (table.isCleanSweep() ? 1 : 0);
    TablePlayer landlord = table.getLandlord();
    boolean landlordWin = landlord.isCardsEmpty();
    for (TablePlayer player : table.getPlayers()) {
      if (player == landlord) {
        continue;
      }
      int fan = tableFan + (player.isDoubling() ? 1 : 0) + (landlord.isDoubling() ? 1 : 0);
      int score = (int) Math.pow(2, fan) * landlord.getBidScore();
      landlord.setScore(landlord.getScore() + (landlordWin ? 1 : -1) * score);
      player.setScore(player.getScore() + (landlordWin ? -1 : 1) * score);
    }
    return DdzPhaseType.END;
  }
}
