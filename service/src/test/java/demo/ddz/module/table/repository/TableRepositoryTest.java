package demo.ddz.module.table.repository;

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.module.table.Table;
import demo.ddz.tests.SpringIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TableRepository")
class TableRepositoryTest {

  @Nested
  @DisplayName("Meta")
  class Meta extends SpringIntegrationTests {

    @Autowired TableRepository.Meta meta;

    @Nested
    @DisplayName("module")
    class Module {

      @Nested
      @DisplayName("when called")
      class WhenCalled {

        @Test
        @DisplayName("then return")
        void thenReturn() {
          assertThat(meta.module()).isEqualTo("model_table");
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
          assertThat(meta.indexId(new Table().setId(1L))).isEqualTo(1L);
        }
      }
    }
  }
}
