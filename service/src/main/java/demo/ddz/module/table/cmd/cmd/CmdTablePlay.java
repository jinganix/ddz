package demo.ddz.module.table.cmd.cmd;

import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.table.cmd.TableCmd;
import java.util.List;
import lombok.Getter;

@Getter
public class CmdTablePlay extends TableCmd {

  private final List<Integer> cardIds;

  public CmdTablePlay(Long playerId, List<Integer> cardIds) {
    super(CmdType.tableBid, playerId);
    this.cardIds = cardIds;
  }
}
