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

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Table")
class TableTest {

  @Nested
  @DisplayName("getKey")
  class GetKey {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return key")
      void thenReturnKey() {
        Table table = new Table().setId(1L);
        assertThat(table.getKey()).isEqualTo("1");
      }
    }
  }

  @Nested
  @DisplayName("setCfg")
  class SetCfg {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then set cfg")
      void thenSetCfg() {
        Table table = new Table().setCfg(null);
        assertThat(table.getCfg()).isNull();
      }
    }
  }

  @Nested
  @DisplayName("reset")
  class Reset {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then reset")
      void thenReset() {
        List<TablePlayer> players = List.of(new TablePlayer());
        Table table =
            new Table()
                .setId(1L)
                .setPlayers(players)
                .setLandlord(new TablePlayer())
                .setHighestBidder(new HighestBidder(1L, Collections.emptyList()))
                .setCursor(2);

        table.reset();

        assertThat(table.getId()).isEqualTo(1L);
        assertThat(table.getDeck()).isNotNull();
        assertThat(table.getPlayers()).hasSize(1);
        assertThat(table.getLandlord()).isNull();
        assertThat(table.getHighestBidder()).isNull();
        assertThat(table.getCursor()).isEqualTo(0);
      }
    }
  }

  @Nested
  @DisplayName("moveCursor")
  class MoveCursor {

    @Nested
    @DisplayName("when cursor is within bound")
    class WhenCursorIsWithinBound {

      @Test
      @DisplayName("then move cursor to next")
      void thenMoveCursorToNext() {
        Table table = new Table();
        table.setPlayers(List.of(new TablePlayer(), new TablePlayer()));
        table.moveCursor();
        assertThat(table.getCursor()).isEqualTo(1);
      }
    }

    @Nested
    @DisplayName("when cursor is out of bound")
    class WhenCursorIsOutOfBound {

      @Test
      @DisplayName("then move cursor to 0")
      void thenMoveCursorTo0() {
        Table table = new Table();
        table.setCursor(1);
        table.setPlayers(List.of(new TablePlayer(), new TablePlayer()));
        table.moveCursor();
        assertThat(table.getCursor()).isEqualTo(0);
      }
    }
  }
}
