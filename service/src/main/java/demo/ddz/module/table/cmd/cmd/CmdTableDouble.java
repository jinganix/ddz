package demo.ddz.module.table.cmd.cmd;

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.table.cmd.TableCmd;
import lombok.Getter;

@Getter
public class CmdTableDouble extends TableCmd {

  private final boolean doubling;

  public CmdTableDouble(Long playerId, boolean doubling) {
    super(CmdType.tableDouble, playerId);
    this.doubling = doubling;
  }
}
