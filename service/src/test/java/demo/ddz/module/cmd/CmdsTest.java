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

package demo.ddz.module.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Cmds")
class CmdsTest {

  @Nested
  @DisplayName("pop")
  class Pop {

    @Nested
    @DisplayName("when empty")
    class WhenEmpty {

      @Test
      @DisplayName("then return null")
      void thenReturn() {
        assertThat(new Cmds().pop()).isNull();
      }
    }

    @Nested
    @DisplayName("when contains 1")
    class WhenContains1 {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        Cmd cmd = mock(Cmd.class);
        Cmds cmds = new Cmds(cmd);
        assertThat(cmds.pop()).isEqualTo(cmd);
        assertThat(cmds.isEmpty()).isTrue();
      }
    }

    @Nested
    @DisplayName("when contains 2")
    class WhenContains2 {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        Cmd cmd = mock(Cmd.class);
        Cmds cmds = new Cmds().pushAll(List.of(cmd, mock(Cmd.class)));
        assertThat(cmds.pop()).isEqualTo(cmd);
        assertThat(cmds.isEmpty()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("result")
  class Result {

    static class CmdImpl extends Cmd {

      protected CmdImpl() {
        super(CmdType.reserved);
      }
    }

    static class ResultImpl extends CmdResult<CmdImpl> {

      protected ResultImpl() {
        super(new CmdImpl());
      }
    }

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then result added")
      void thenResultAdded() {
        Cmds cmds = new Cmds();

        cmds.result(new ResultImpl());
        assertThat(cmds.getResults()).hasSize(1);
        ResultImpl result = (ResultImpl) cmds.getResults().get(0);
        assertThat(result.getCmdType()).isEqualTo(CmdType.reserved);
      }
    }
  }
}
