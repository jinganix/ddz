package demo.ddz.module.table.cmd.result;

import demo.ddz.module.cmd.CmdResult;
import demo.ddz.module.table.cmd.cmd.CmdTableFold;
import lombok.Getter;

@Getter
public class CmdTableFoldResult extends CmdResult<CmdTableFold> {

  public CmdTableFoldResult(CmdTableFold cmd) {
    super(cmd);
  }
}
