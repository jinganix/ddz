package demo.ddz.module.cmd;

import lombok.Getter;

@Getter
public abstract class CmdResult<C extends Cmd> {

  private C cmd;

  protected CmdResult(C cmd) {
    this.cmd = cmd;
  }

  public CmdType getCmdType() {
    return this.cmd.getCmdType();
  }
}
