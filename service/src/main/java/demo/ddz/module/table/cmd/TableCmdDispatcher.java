package demo.ddz.module.table.cmd;

import demo.ddz.helper.actor.ChainedTaskExecutor;
import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.cmd.Cmd;
import demo.ddz.module.cmd.CmdDispatcher;
import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.player.Player;
import demo.ddz.module.player.PlayerRepository;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.repository.TableRepository;
import demo.ddz.module.utils.ErrorCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableCmdDispatcher implements CmdDispatcher {

  private final Map<CmdType, TableCmdExecutor<Cmd>> executors = new HashMap<>();

  private final ChainedTaskExecutor chainedTaskExecutor;

  private final PlayerRepository playerRepository;

  private final TableRepository tableRepository;

  @Autowired
  @SuppressWarnings("unchecked")
  void setExecutors(List<TableCmdExecutor<? extends Cmd>> executors) {
    for (TableCmdExecutor<? extends Cmd> executor : executors) {
      this.executors.put(executor.support(), (TableCmdExecutor<Cmd>) executor);
    }
  }

  @Override
  public boolean support(Cmd cmd) {
    return executors.containsKey(cmd.getCmdType());
  }

  @Override
  public void dispatch(Long playerId, Cmds cmds, Cmd cmd) {
    Player player = playerRepository.find(playerId);
    Long tableId = player.getTableId();
    Table table = tableRepository.find(tableId);
    if (table == null) {
      throw BusinessException.of(ErrorCode.TABLE_NOT_FOUND);
    }
    TableCmdExecutor<Cmd> executor = executors.get(cmd.getCmdType());
    chainedTaskExecutor.executeSync(tableId, () -> executor.execute(tableId, cmds, cmd));
  }
}
