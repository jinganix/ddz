package demo.ddz.module.table.cmd.result;

import demo.ddz.module.cmd.CmdResult;
import demo.ddz.module.poker.PokerHand;
import demo.ddz.module.table.cmd.cmd.CmdTablePlay;
import lombok.Getter;

@Getter
public class CmdTablePlayResult extends CmdResult<CmdTablePlay> {

  private final PokerHand pokerHand;

  public CmdTablePlayResult(CmdTablePlay cmd, PokerHand pokerHand) {
    super(cmd);
    this.pokerHand = pokerHand;
  }
}
