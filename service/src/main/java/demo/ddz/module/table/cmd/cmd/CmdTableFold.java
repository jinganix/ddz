package demo.ddz.module.table.cmd.cmd;

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.table.cmd.TableCmd;
import lombok.Getter;

@Getter
public class CmdTableFold extends TableCmd {

  public CmdTableFold(Long playerId) {
    super(CmdType.tableFold, playerId);
  }
}
