package demo.ddz.module.table.cmd.executor;

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.PlayerState;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TableExecutor;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.table.cmd.TableCmdChecker;
import demo.ddz.module.table.cmd.TableCmdExecutor;
import demo.ddz.module.table.cmd.cmd.CmdTableDouble;
import demo.ddz.module.table.cmd.result.CmdTableDoubleResult;
import demo.ddz.module.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CmdTableDoubleExecutor extends TableCmdExecutor<CmdTableDouble> {

  private final TableExecutor tableExecutor;

  private final TableRepository tableRepository;

  @Override
  public CmdType support() {
    return CmdType.tableDouble;
  }

  @Override
  public void execute(Long tableId, Cmds cmds, CmdTableDouble cmd) {
    Table table = tableRepository.find(tableId);
    TableCmdChecker.assertExecution(cmd.getPlayerId(), table, DdzPhaseType.DOUBLING);
    TablePlayer player = table.getCurrentPlayer();
    player.setDoubling(true).setState(PlayerState.PLAYING);
    if (table.getPlayers().stream().allMatch(e -> e.isState(PlayerState.PLAYING))) {
      tableExecutor.execute(table, DdzPhaseType.DOUBLING);
    }
    cmds.result(new CmdTableDoubleResult(cmd));
  }
}
