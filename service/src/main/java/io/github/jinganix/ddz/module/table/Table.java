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

package io.github.jinganix.ddz.module.table;

import io.github.jinganix.ddz.helper.phase.PhasedContext;
import io.github.jinganix.ddz.helper.phase.ScheduledPhase;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@Getter
@Setter
@Accessors(chain = true)
public class Table implements PhasedContext {

  @Delegate private final ScheduledPhase phase = new ScheduledPhase(this);

  private final CardDeck deck = new CardDeck();

  private Long id;

  private TableCfg cfg = new TableCfg();

  private List<TablePlayer> players = new ArrayList<>();

  private TablePlayer landlord;

  private HighestBidder highestBidder;

  private int cursor;

  private boolean cleanSweep = true;

  private int bombCount;

  @Override
  public String getKey() {
    return String.valueOf(id);
  }

  public TablePlayer getCurrentPlayer() {
    return players.get(cursor);
  }

  public void reset() {
    this.deck.reset();
    this.players.forEach(TablePlayer::reset);
    this.landlord = null;
    this.highestBidder = null;
    this.cursor = 0;
    this.cleanSweep = true;
    this.bombCount = 0;
  }

  public void moveCursor() {
    this.cursor++;
    if (this.cursor >= players.size()) {
      this.cursor = 0;
    }
  }
}
