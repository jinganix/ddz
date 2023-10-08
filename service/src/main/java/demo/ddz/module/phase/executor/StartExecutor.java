/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
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
import demo.ddz.helper.utils.UtilsService;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartExecutor extends PhaseExecutor<Table> {

  private final UtilsService utilsService;

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.START;
  }

  @Override
  public long schedule(Table table) {
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    if (table.getPlayers().stream().filter(e -> e.isState(PlayerState.READY)).count() != 3) {
      return DdzPhaseType.IDLE;
    }
    table.getPlayers().forEach(e -> e.setState(PlayerState.BIDDING));
    table.setCursor(utilsService.nextInt(0, table.getPlayers().size()));
    table.setHighestBidder(null);
    return DdzPhaseType.BIDDING;
  }
}
