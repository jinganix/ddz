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

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.module.ai.AutoPlay;
import demo.ddz.module.ai.ShallowMind;
import demo.ddz.module.poker.Card;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TablePlayer")
class TablePlayerTest {

  @Nested
  @DisplayName("disableAuto")
  class DisableAuto {

    @Nested
    @DisplayName("when auto play is null")
    class WhenAutoPlayIsNull {

      @Test
      @DisplayName("then clear")
      void thenClear() {
        TablePlayer player = new TablePlayer().setAuto(1);
        player.disableAuto();
        assertThat(player.getAuto()).isEqualTo(0);
        assertThat(player.getAutoPlay()).isNull();
        assertThat(player.getCards()).isNull();
      }
    }

    @Nested
    @DisplayName("when auto play is not null")
    class WhenAutoPlayIsNotNull {

      @Test
      @DisplayName("then clear")
      void thenClear() {
        TablePlayer player =
            new TablePlayer().setAuto(1).setAutoPlay(new ShallowMind(List.of(new Card(1))));
        player.disableAuto();
        assertThat(player.getAuto()).isEqualTo(0);
        assertThat(player.getAutoPlay()).isNull();
        assertThat(player.getCards())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new Card(1));
      }
    }
  }

  @Nested
  @DisplayName("incrAuto")
  class IncrAuto {

    @Nested
    @DisplayName("when auto is 0")
    class WhenAutoIs0 {

      @Test
      @DisplayName("then increment")
      void thenIncrement() {
        TablePlayer player = new TablePlayer().setAuto(0).setCards(List.of(new Card(1)));
        player.incrAuto();
        assertThat(player.getAuto()).isEqualTo(1);
        assertThat(player.getAutoPlay()).isNull();
        assertThat(player.getCards())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new Card(1));
      }
    }

    @Nested
    @DisplayName("when auto is 1")
    class WhenAutoIs1 {

      @Nested
      @DisplayName("when auto play is null")
      class WhenAutoPlayIsNull {

        @Test
        @DisplayName("then auto play")
        void thenAutoPlay() {
          TablePlayer player = new TablePlayer().setAuto(1).setCards(List.of(new Card(1)));
          player.incrAuto();
          assertThat(player.getAuto()).isEqualTo(2);
          assertThat(player.getAutoPlay()).isNotNull();
          assertThat(player.getAutoPlay().toCards())
              .usingRecursiveFieldByFieldElementComparator()
              .containsExactly(new Card(1));
          assertThat(player.getCards()).isNull();
        }
      }

      @Nested
      @DisplayName("when auto play is not null")
      class WhenAutoPlayIsNotNull {

        @Test
        @DisplayName("then auto play")
        void thenAutoPlay() {
          AutoPlay autoPlay = new ShallowMind(List.of(new Card(1)));
          TablePlayer player = new TablePlayer().setAuto(1).setAutoPlay(autoPlay);
          player.incrAuto();
          assertThat(player.getAuto()).isEqualTo(2);
          assertThat(player.getAutoPlay()).isNotNull();
          assertThat(player.getAutoPlay()).isEqualTo(autoPlay);
          assertThat(player.getCards()).isNull();
        }
      }
    }
  }

  @Nested
  @DisplayName("enableAuto")
  class EnableAuto {

    @Nested
    @DisplayName("when auto play is null")
    class WhenAutoPlayIsNull {

      @Test
      @DisplayName("then enable")
      void thenEnable() {
        TablePlayer player = new TablePlayer().setAuto(1).setCards(List.of(new Card(1)));
        player.enableAuto();
        assertThat(player.getAutoPlay()).isNotNull();
        assertThat(player.getAutoPlay().toCards())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new Card(1));
        assertThat(player.getCards()).isNull();
      }
    }

    @Nested
    @DisplayName("when auto play is not null")
    class WhenAutoPlayIsNotNull {

      @Test
      @DisplayName("then auto play")
      void thenEnable() {
        AutoPlay autoPlay = new ShallowMind(List.of(new Card(1)));
        TablePlayer player = new TablePlayer().setAuto(1).setAutoPlay(autoPlay);
        player.enableAuto();
        assertThat(player.getAutoPlay()).isNotNull();
        assertThat(player.getAutoPlay()).isEqualTo(autoPlay);
        assertThat(player.getCards()).isNull();
      }
    }
  }
}
