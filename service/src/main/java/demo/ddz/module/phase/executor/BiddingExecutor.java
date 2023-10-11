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

package demo.ddz.module.phase.executor;

import demo.ddz.helper.phase.PhaseExecutor;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BiddingExecutor extends PhaseExecutor<Table> {

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.BIDDING;
  }

  @Override
  public long schedule(Table table) {
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    if (table.getLandlord() != null) {
      return DdzPhaseType.DOUBLING;
    }
    TablePlayer current = table.getCurrentPlayer();
    if (current.isState(PlayerState.BIDDING)) {
      current.setAuto(current.getAuto() + 1);
      current.setState(PlayerState.DOUBLING);
      current.setBidScore(0);
    }
    if (table.getPlayers().stream().allMatch(e -> e.isState(PlayerState.DOUBLING))) {
      if (table.getPlayers().stream().allMatch(e -> e.getBidScore() == 0)) {
        return DdzPhaseType.END;
      }
      Optional<TablePlayer> max =
          table.getPlayers().stream().max(Comparator.comparingInt(TablePlayer::getBidScore));
      table.setLandlord(max.get());
      table.setCursor(table.getPlayers().indexOf(table.getLandlord()));
      return DdzPhaseType.DOUBLING;
    }
    table.moveCursor();
    return DdzPhaseType.BIDDING;
  }
}
