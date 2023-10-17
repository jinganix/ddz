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
import io.github.jinganix.ddz.module.poker.Card;
import io.github.jinganix.ddz.module.poker.CardsSet;
import io.github.jinganix.ddz.module.poker.PokerHand;
import io.github.jinganix.ddz.module.table.HighestBidder;
import io.github.jinganix.ddz.module.table.PlayerState;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TablePlayer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayingExecutor extends PhaseExecutor<Table> {

  @Override
  public DdzPhaseType getPhaseType() {
    return DdzPhaseType.PLAYING;
  }

  @Override
  public long schedule(Table table) {
    TablePlayer current = table.getCurrentPlayer();
    if (current.isState(PlayerState.FOLD)) {
      return 0;
    }
    if (table.getHighestBidder() != null && table.getHighestBidder().equalsTo(current)) {
      table.setHighestBidder(null);
      for (TablePlayer player : table.getPlayers()) {
        player.setState(PlayerState.PLAYING);
      }
    }
    return table.getCfg().getDuration(getPhaseType());
  }

  @Override
  public DdzPhaseType execute(Table table) {
    TablePlayer current = table.getCurrentPlayer();
    if (current.isState(PlayerState.FOLD)) {
      table.moveCursor();
      return DdzPhaseType.PLAYING;
    }
    if (table.getHighestBidder() == null) {
      current.enableAuto();
      autoPlay(table, current);
    } else if (!table.getHighestBidder().equalsTo(current)) {
      current.incrAuto();
      if (current.getAutoPlay() != null) {
        autoPlay(table, current);
      } else {
        current.setState(PlayerState.FOLD);
      }
    }
    if (table.getHighestBidder().equalsTo(current)) {
      PokerHand pokerHand = table.getHighestBidder().cardsSet().getPokerHand();
      if (pokerHand == PokerHand.FOUR_OF_KIND || pokerHand == PokerHand.ROCKET) {
        table.setBombCount(table.getBombCount() + 1);
      }
    }
    if (!table.getHighestBidder().equalsTo(table.getLandlord())) {
      table.setCleanSweep(false);
    }
    if (current.isCardsEmpty()) {
      return DdzPhaseType.SETTLEMENT;
    }
    table.moveCursor();
    return DdzPhaseType.PLAYING;
  }

  private void autoPlay(Table table, TablePlayer player) {
    List<Card> cards =
        table.getHighestBidder() == null
            ? player.getAutoPlay().leadOff()
            : player.getAutoPlay().followSuit(table.getHighestBidder().cardsSet());
    if (!cards.isEmpty()) {
      CardsSet cardsSet = new CardsSet(cards);
      table.setHighestBidder(new HighestBidder(player.getId(), cardsSet));
    }
  }
}
