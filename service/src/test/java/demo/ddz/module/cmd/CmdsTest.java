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
