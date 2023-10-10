package demo.ddz.module.table.cmd.result;

import demo.ddz.module.cmd.CmdResult;
import demo.ddz.module.table.cmd.cmd.CmdTableBid;
import lombok.Getter;

@Getter
public class CmdTableBidResult extends CmdResult<CmdTableBid> {

  public CmdTableBidResult(CmdTableBid cmd) {
    super(cmd);
  }
}
