package demo.ddz.module.table.cmd;

import demo.ddz.module.cmd.Cmd;
import demo.ddz.module.cmd.CmdType;
import lombok.Getter;

@Getter
public class TableCmd extends Cmd {

  private final Long playerId;

  protected TableCmd(CmdType cmdType, Long playerId) {
    super(cmdType);
    this.playerId = playerId;
  }
}
