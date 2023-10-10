package demo.ddz.helper.repository.single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractSingleValueRepository")
class AbstractSingleValueRepositoryTest {

  static class Repository extends AbstractSingleValueRepository<Integer, Integer> {

    public Repository(SingleValueRepositoryMeta<Integer, Integer> meta) {
      super(meta, null);
    }

    @Override
    public Integer save(Integer entity) {
      return null;
    }

    @Override
    public Integer deleteById(Integer id) {
      return 123;
    }

    @Override
    public Integer find(Integer id) {
      return null;
    }

    @Override
    public List<Integer> findAll(List<Integer> ids) {
      return null;
    }
  }

  SingleValueRepositoryMeta<Integer, Integer> meta =
      spy(
          new SingleValueRepositoryMeta<>() {
            @Override
            public String module() {
              return "test";
            }

            @Override
            public Integer indexId(Integer entity) {
              return 1;
            }
          });

  Repository repository = new Repository(meta);

  @Nested
  @DisplayName("timer")
  class Timer {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call meta")
      void thenCallMeta() {
        assertThat(repository.module()).isEqualTo("test");
        verify(meta, times(1)).module();
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
      @DisplayName("then call meta")
      void thenCallMeta() {
        assertThat(repository.indexId(0)).isEqualTo(1);
        verify(meta, times(1)).indexId(0);
      }
    }
  }

  @Nested
  @DisplayName("delete")
  class Delete {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then delete by id")
      void thenCallDelegate() {
        Integer value = repository.delete(0);
        verify(meta, times(1)).indexId(0);
        assertThat(value).isEqualTo(123);
      }
    }
  }
}
