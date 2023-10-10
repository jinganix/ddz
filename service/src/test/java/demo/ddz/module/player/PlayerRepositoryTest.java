package demo.ddz.module.player;

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.tests.SpringIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PlayerRepository")
class PlayerRepositoryTest {
  @Nested
  @DisplayName("Meta")
  class Meta extends SpringIntegrationTests {

    @Autowired PlayerRepository.Meta meta;

    @Nested
    @DisplayName("module")
    class Module {

      @Nested
      @DisplayName("when called")
      class WhenCalled {

        @Test
        @DisplayName("then return")
        void thenReturn() {
          assertThat(meta.module()).isEqualTo("model_player");
        }
      }
    }

    @Nested
    @DisplayName("indexId")
    class IndexId {

      @Nested
      @DisplayName("when called")
      class WhenCalled {

        @Test
        @DisplayName("then return")
        void thenReturn() {
          assertThat(meta.indexId(new Player().setId(1L))).isEqualTo(1L);
        }
      }
    }
  }
}
