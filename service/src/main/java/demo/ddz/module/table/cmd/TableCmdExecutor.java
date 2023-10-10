package demo.ddz.module.table.cmd;

import demo.ddz.module.cmd.Cmd;
import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;

public abstract class TableCmdExecutor<C extends Cmd> {

  public abstract CmdType support();

  public abstract void execute(Long tableId, Cmds cmds, C cmd);
}
