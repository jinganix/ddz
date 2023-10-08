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

package demo.ddz.module.ai;

import demo.ddz.module.poker.Card;
import demo.ddz.module.poker.CardsSet;
import java.util.List;

public interface AutoPlay {

  boolean isEmpty();

  List<Card> toCards();

  List<Card> leadOff();

  List<Card> followSuit(CardsSet cardsSet);
}
