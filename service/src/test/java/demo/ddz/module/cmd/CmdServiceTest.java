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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CmdService")
class CmdServiceTest {

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when no supported dispatcher")
    class WhenNoSupportedDispatcher {

      @Test
      @DisplayName("then throw error")
      void thenThrowError() {
        CmdDispatcher dispatcher = mock(CmdDispatcher.class);
        when(dispatcher.support(any())).thenReturn(false);
        CmdService cmdService = new CmdService(List.of(dispatcher));

        assertThatThrownBy(() -> cmdService.execute(1L, new Cmds(mock(Cmd.class))))
            .isInstanceOf(RuntimeException.class);
      }
    }

    @Nested
    @DisplayName("when supported dispatcher found")
    class WhenThereSupportedDispatcherFound {

      @Test
      @DisplayName("then dispatch")
      void thenDispatch() {
        CmdDispatcher dispatcher = mock(CmdDispatcher.class);
        when(dispatcher.support(any())).thenReturn(true);
        CmdService cmdService = new CmdService(List.of(dispatcher));

        Cmd cmd = mock(Cmd.class);
        Cmds cmds = new Cmds(cmd);
        cmdService.execute(1L, cmds);
        verify(dispatcher, times(1)).dispatch(1L, cmds, cmd);
      }
    }
  }
}
