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
import io.github.jinganix.ddz.helper.utils.UtilsService;
import io.github.jinganix.ddz.module.ai.ShallowMind;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.poker.Card;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealingExecutor extends PhaseExecutor<Table> {

  private final UtilsService utilsService;

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.DEALING;
  }

  @Override
  public long schedule(Table table) {
    for (TablePlayer player : table.getPlayers()) {
      List<Card> cards =
          IntStream.rangeClosed(1, 17)
              .mapToObj(e -> table.getDeck().pop())
              .collect(Collectors.toList());
      if (player.getAuto() >= 2) {
        player.setAutoPlay(new ShallowMind(cards));
      } else {
        player.setCards(cards);
      }
    }
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    table.getPlayers().forEach(player -> player.setState(PlayerState.BIDDING));
    table.setCursor(utilsService.nextInt(0, table.getPlayers().size()));
    table.setHighestBidder(null);
    return DdzPhaseType.BIDDING;
  }
}
