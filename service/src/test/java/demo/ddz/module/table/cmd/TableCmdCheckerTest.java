package demo.ddz.module.table.cmd;

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

@DisplayName("TableCmdChecker")
class TableCmdCheckerTest {

  @Nested
  @DisplayName("assertExecution")
  class AssertExecution {

    @Nested
    @DisplayName("when table is null")
    class WhenTableIsNull {

      @Test
      @DisplayName("then throw TABLE_NOT_FOUND")
      void thenThrow() {
        assertThatThrownBy(() -> TableCmdChecker.assertExecution(0L, null, DdzPhaseType.IDLE))
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
        assertThatThrownBy(() -> TableCmdChecker.assertExecution(0L, table, DdzPhaseType.IDLE))
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
        Table table = new Table().setPlayers(List.of(new TablePlayer().setId(1L)));
        table.setPhaseType(DdzPhaseType.IDLE);
        assertThatThrownBy(() -> TableCmdChecker.assertExecution(0L, table, DdzPhaseType.IDLE))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.NOT_CURRENT_PLAYER);
      }
    }
  }
}
