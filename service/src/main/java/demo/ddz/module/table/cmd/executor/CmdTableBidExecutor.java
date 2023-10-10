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
import demo.ddz.module.table.cmd.cmd.CmdTableBid;
import demo.ddz.module.table.cmd.result.CmdTableBidResult;
import demo.ddz.module.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CmdTableBidExecutor extends TableCmdExecutor<CmdTableBid> {

  private final TableExecutor tableExecutor;

  private final TableRepository tableRepository;

  @Override
  public CmdType support() {
    return CmdType.tableBid;
  }

  @Override
  public void execute(Long tableId, Cmds cmds, CmdTableBid cmd) {
    Table table = tableRepository.find(tableId);
    TableCmdChecker.assertExecution(cmd.getPlayerId(), table, DdzPhaseType.BIDDING);
    TablePlayer player = table.getCurrentPlayer();
    player.setBidScore(cmd.getScore()).setState(PlayerState.DOUBLING);
    tableExecutor.execute(table, DdzPhaseType.BIDDING);
    cmds.result(new CmdTableBidResult(cmd));
  }
}
