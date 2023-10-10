package demo.ddz.module.cmd;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Cmds {

  private final List<Cmd> cmds = new ArrayList<>();

  private final List<CmdResult<? extends Cmd>> results = new ArrayList<>();

  private int index = 0;

  public Cmds() {}

  public Cmds(Cmd... cmds) {
    for (Cmd cmd : cmds) {
      this.push(cmd);
    }
  }

  public Cmds push(Cmd cmd) {
    this.cmds.add(cmd);
    return this;
  }

  public Cmds pushAll(List<Cmd> cmds) {
    this.cmds.addAll(cmds);
    return this;
  }

  public Cmd pop() {
    return isEmpty() ? null : cmds.get(index++);
  }

  public boolean isEmpty() {
    return index >= cmds.size();
  }

  public Cmds result(CmdResult<? extends Cmd> result) {
    this.results.add(result);
    return this;
  }
}
