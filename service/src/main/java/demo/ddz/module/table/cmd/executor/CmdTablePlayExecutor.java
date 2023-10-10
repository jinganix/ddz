package demo.ddz.module.table.cmd.executor;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.cmd.CmdType;
import demo.ddz.module.cmd.Cmds;
import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.poker.Card;
import demo.ddz.module.poker.CardsSet;
import demo.ddz.module.table.HighestBidder;
import demo.ddz.module.table.Table;
import demo.ddz.module.table.TableExecutor;
import demo.ddz.module.table.TablePlayer;
import demo.ddz.module.table.cmd.TableCmdChecker;
import demo.ddz.module.table.cmd.TableCmdExecutor;
import demo.ddz.module.table.cmd.cmd.CmdTablePlay;
import demo.ddz.module.table.cmd.result.CmdTablePlayResult;
import demo.ddz.module.table.repository.TableRepository;
import demo.ddz.module.utils.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CmdTablePlayExecutor extends TableCmdExecutor<CmdTablePlay> {

  private final TableExecutor tableExecutor;

  private final TableRepository tableRepository;

  @Override
  public CmdType support() {
    return CmdType.tableFold;
  }

  @Override
  public void execute(Long tableId, Cmds cmds, CmdTablePlay cmd) {
    Table table = tableRepository.find(tableId);
    TableCmdChecker.assertExecution(cmd.getPlayerId(), table, DdzPhaseType.PLAYING);
    TablePlayer player = table.getCurrentPlayer();
    player.disableAuto();
    List<Card> playedCards = new ArrayList<>(cmd.getCardIds().size());
    List<Card> remainingCards = new ArrayList<>(player.getCards().size() - cmd.getCardIds().size());
    for (Card card : player.getCards()) {
      if (cmd.getCardIds().contains(card.getId())) {
        playedCards.add(card);
      } else {
        remainingCards.add(card);
      }
    }
    if (playedCards.size() != cmd.getCardIds().size()) {
      throw BusinessException.of(ErrorCode.INVALID_PLAYED_CARDS);
    }
    CardsSet cardsSet = new CardsSet(playedCards);
    if (cardsSet.getPokerHand() == null) {
      throw BusinessException.of(ErrorCode.INVALID_PLAYED_CARDS);
    }
    CardsSet highest =
        table.getHighestBidder() == null ? null : table.getHighestBidder().cardsSet();
    if (highest != null && !cardsSet.dominate(highest)) {
      throw BusinessException.of(ErrorCode.CARDS_SET_NOT_DOMINATED);
    }
    player.setCards(remainingCards);
    table.setHighestBidder(new HighestBidder(cmd.getPlayerId(), cardsSet));
    tableExecutor.execute(table, DdzPhaseType.PLAYING);
    cmds.result(new CmdTablePlayResult(cmd, cardsSet.getPokerHand()));
  }
}
