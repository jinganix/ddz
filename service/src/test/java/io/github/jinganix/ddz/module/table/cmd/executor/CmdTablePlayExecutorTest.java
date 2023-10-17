/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jinganix.ddz.module.table.cmd.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.helper.exception.BusinessException;
import io.github.jinganix.ddz.module.cmd.CmdType;
import io.github.jinganix.ddz.module.cmd.Cmds;
import io.github.jinganix.ddz.module.phase.DdzPhaseType;
import io.github.jinganix.ddz.module.poker.Card;
import io.github.jinganix.ddz.module.poker.CardsSet;
import io.github.jinganix.ddz.module.poker.PokerHand;
import io.github.jinganix.ddz.module.table.HighestBidder;
import io.github.jinganix.ddz.module.table.Table;
import io.github.jinganix.ddz.module.table.TableExecutor;
import io.github.jinganix.ddz.module.table.TablePlayer;
import io.github.jinganix.ddz.module.table.cmd.TableCmdChecker;
import io.github.jinganix.ddz.module.table.cmd.cmd.CmdTablePlay;
import io.github.jinganix.ddz.module.table.cmd.result.CmdTablePlayResult;
import io.github.jinganix.ddz.module.table.repository.TableRepository;
import io.github.jinganix.ddz.module.utils.ErrorCode;
import io.github.jinganix.ddz.tests.TestConst;
import java.util.Collections;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CmdTablePlayExecutor")
@ExtendWith(MockitoExtension.class)
class CmdTablePlayExecutorTest {

  @Mock TableCmdChecker tableCmdChecker;

  @Mock TableExecutor tableExecutor;

  @Mock TableRepository tableRepository;

  @InjectMocks CmdTablePlayExecutor cmdTablePlayExecutor;

  @Nested
  @DisplayName("support")
  class Support {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(cmdTablePlayExecutor.support()).isEqualTo(CmdType.tablePlay);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when played non exists cards")
    class WhenPlayedNonExistsCards {

      @Test
      @DisplayName("then throw INVALID_PLAYED_CARDS")
      void thenThrow() {
        TablePlayer player = new TablePlayer().setCards(Collections.emptyList());
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTablePlay cmd = new CmdTablePlay(TestConst.UID_1, List.of(1));
        assertThatThrownBy(() -> cmdTablePlayExecutor.execute(TestConst.UID_1, cmds, cmd))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.INVALID_PLAYED_CARDS);
        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
      }
    }

    @Nested
    @DisplayName("when cards not poker hand")
    class WhenCardsNotPokerHand {

      @Test
      @DisplayName("then throw INVALID_PLAYED_CARDS")
      void thenThrow() {
        TablePlayer player = new TablePlayer().setCards(List.of(new Card(1), new Card(5)));
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTablePlay cmd = new CmdTablePlay(TestConst.UID_1, List.of(1, 5));
        assertThatThrownBy(() -> cmdTablePlayExecutor.execute(TestConst.UID_1, cmds, cmd))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.INVALID_PLAYED_CARDS);
        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
      }
    }

    @Nested
    @DisplayName("when cards not dominating")
    class WhenCardsNotDominating {

      @Test
      @DisplayName("then throw PLAYED_CARDS_NOT_DOMINATING")
      void thenThrow() {
        TablePlayer player = new TablePlayer().setCards(List.of(new Card(6)));
        Table table =
            new Table()
                .setPlayers(List.of(player))
                .setHighestBidder(
                    new HighestBidder(TestConst.UID_2, new CardsSet(newArrayList(new Card(8)))));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTablePlay cmd = new CmdTablePlay(TestConst.UID_1, List.of(6));
        assertThatThrownBy(() -> cmdTablePlayExecutor.execute(TestConst.UID_1, cmds, cmd))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.PLAYED_CARDS_NOT_DOMINATING);
        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
      }
    }

    @Nested
    @DisplayName("when cards dominating")
    class WhenCardsDominating {

      @Test
      @DisplayName("then play cards")
      void thenPlayCards() {
        TablePlayer player = new TablePlayer().setCards(List.of(new Card(9), new Card(10)));
        Table table =
            new Table()
                .setPlayers(List.of(player))
                .setHighestBidder(
                    new HighestBidder(TestConst.UID_2, new CardsSet(newArrayList(new Card(8)))));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTablePlay cmd = new CmdTablePlay(TestConst.UID_1, List.of(9));
        cmdTablePlayExecutor.execute(TestConst.UID_1, cmds, cmd);

        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
        verify(tableExecutor, times(1)).execute(table, DdzPhaseType.PLAYING);
        assertThat(player.getCards())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new Card(10));
        assertThat(table.getHighestBidder())
            .usingRecursiveComparison()
            .isEqualTo(
                new HighestBidder(TestConst.UID_1, new CardsSet(Lists.newArrayList(new Card(9)))));
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTablePlayResult(cmd, PokerHand.SINGLE));
      }
    }

    @Nested
    @DisplayName("when no highest bidder")
    class WhenNoHighestBidder {

      @Test
      @DisplayName("then play cards")
      void thenPlayCards() {
        TablePlayer player = new TablePlayer().setCards(List.of(new Card(6)));
        Table table = new Table().setPlayers(List.of(player));
        when(tableRepository.find(TestConst.UID_1)).thenReturn(table);

        Cmds cmds = new Cmds();
        CmdTablePlay cmd = new CmdTablePlay(TestConst.UID_1, List.of(6));
        cmdTablePlayExecutor.execute(TestConst.UID_1, cmds, cmd);

        verify(tableCmdChecker, times(1))
            .assertExecution(TestConst.UID_1, table, DdzPhaseType.PLAYING);
        verify(tableExecutor, times(1)).execute(table, DdzPhaseType.PLAYING);
        assertThat(cmds.getResults())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new CmdTablePlayResult(cmd, PokerHand.SINGLE));
      }
    }
  }
}
