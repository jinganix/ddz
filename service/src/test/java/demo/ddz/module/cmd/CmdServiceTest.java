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
