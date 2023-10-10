package demo.ddz.module.cmd;

public interface CmdDispatcher {

  boolean support(Cmd cmd);

  void dispatch(Long playerId, Cmds cmds, Cmd cmd);
}
