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

package demo.ddz.module.table.cmd;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.utils.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("TableCmdChecker")
@ExtendWith(MockitoExtension.class)
class TableCmdCheckerTest {

  @InjectMocks TableCmdChecker tableCmdChecker;

  @Nested
  @DisplayName("assertExecution")
  class AssertExecution {

    @Nested
    @DisplayName("when table is null")
    class WhenTableIsNull {

      @Test
      @DisplayName("then throw TABLE_NOT_FOUND")
      void thenThrow() {
        assertThatThrownBy(() -> tableCmdChecker.assertExecution(0L, null, DdzPhaseType.IDLE))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.TABLE_NOT_FOUND);
      }
    }

    @Nested
    @DisplayName("when table phase not match")
    class WhenTablePhaseNotMatch {

      @Test
      @DisplayName("then throw PHASE_INVALID")
      void thenThrow() {
        Table table = new Table();
        table.setPhaseType(DdzPhaseType.PLAYING);
        assertThatThrownBy(() -> tableCmdChecker.assertExecution(0L, table, DdzPhaseType.IDLE))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.PHASE_INVALID);
      }
    }

    @Nested
    @DisplayName("when current player not match")
    class WhenCurrentPlayerNotMatch {

      @Test
      @DisplayName("then throw NOT_CURRENT_PLAYER")
      void thenThrow() {
        Table table = new Table().setPlayers(List.of(new TablePlayer().setId(UID_1)));
        table.setPhaseType(DdzPhaseType.IDLE);
        assertThatThrownBy(() -> tableCmdChecker.assertExecution(0L, table, DdzPhaseType.IDLE))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.NOT_CURRENT_PLAYER);
      }
    }

    @Nested
    @DisplayName("when current player match")
    class WhenCurrentPlayerMatch {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        Table table = new Table().setPlayers(List.of(new TablePlayer().setId(UID_1)));
        table.setPhaseType(DdzPhaseType.IDLE);
        assertThatCode(() -> tableCmdChecker.assertExecution(UID_1, table, DdzPhaseType.IDLE))
            .doesNotThrowAnyException();
      }
    }
  }
}
