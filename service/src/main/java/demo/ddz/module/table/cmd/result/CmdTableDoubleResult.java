package demo.ddz.module.table.cmd.result;

import demo.ddz.module.cmd.CmdResult;
import demo.ddz.module.table.cmd.cmd.CmdTableDouble;
import lombok.Getter;

@Getter
public class CmdTableDoubleResult extends CmdResult<CmdTableDouble> {

  public CmdTableDoubleResult(CmdTableDouble cmd) {
    super(cmd);
  }
}
