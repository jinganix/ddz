package demo.ddz.module.table.cmd;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.utils.ErrorCode;
import java.util.Objects;

public class TableCmdChecker {

  public static void assertExecution(Long playerId, Table table, DdzPhaseType phaseType) {
    if (table == null) {
      throw BusinessException.of(ErrorCode.TABLE_NOT_FOUND);
    }
    if (table.getPhaseType() != phaseType) {
      throw BusinessException.of(ErrorCode.PHASE_INVALID);
    }
    TablePlayer player = table.getCurrentPlayer();
    if (!Objects.equals(player.getId(), playerId)) {
      throw BusinessException.of(ErrorCode.NOT_CURRENT_PLAYER);
    }
  }
}
