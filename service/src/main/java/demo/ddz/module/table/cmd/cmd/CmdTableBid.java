package demo.ddz.module.table.cmd.cmd;

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.table.cmd.TableCmd;
import lombok.Getter;

@Getter
public class CmdTableBid extends TableCmd {

  private final int score;

  public CmdTableBid(Long playerId, int score) {
    super(CmdType.tableBid, playerId);
    this.score = score;
  }
}
