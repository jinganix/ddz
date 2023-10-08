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

package demo.ddz.module.table;

import demo.ddz.module.ai.AutoPlay;
import demo.ddz.module.ai.ShallowMind;
import demo.ddz.module.poker.Card;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TablePlayer {

  private Long id;

  private int bidScore;

  private PlayerState state = PlayerState.IDLE;

  private List<Card> cards;

  private AutoPlay autoPlay;

  private int auto;

  private boolean doubling;

  public void reset() {
    this.bidScore = 0;
    this.state = PlayerState.IDLE;
    this.cards = null;
    this.autoPlay = null;
    this.doubling = false;
  }

  public boolean isState(PlayerState state) {
    return this.state == state;
  }

  public void clearAuto() {
    this.auto = 0;
    if (this.autoPlay != null) {
      this.cards = this.autoPlay.toCards();
      this.autoPlay = null;
    }
  }

  public void incrAuto() {
    this.auto++;
    if (auto >= 2 && this.autoPlay == null) {
      this.autoPlay = new ShallowMind(this.cards);
      this.cards = null;
    }
  }

  public boolean isCardsEmpty() {
    return cards == null ? autoPlay.isEmpty() : cards.isEmpty();
  }
}
