package demo.ddz.module.cmd;

import lombok.Getter;

@Getter
public abstract class Cmd {

  private final CmdType cmdType;

  protected Cmd(CmdType cmdType) {
    this.cmdType = cmdType;
  }
}
