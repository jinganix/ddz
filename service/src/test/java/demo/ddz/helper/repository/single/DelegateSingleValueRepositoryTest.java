package demo.ddz.helper.repository.single;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DelegateSingleValueRepository")
class DelegateSingleValueRepositoryTest {

  static class Repository extends DelegateSingleValueRepository<Integer, Integer> {

    protected Repository(SingleValueRepository<Integer, Integer> delegate) {
      super(null, delegate);
    }
  }

  @Mock SingleValueRepository<Integer, Integer> delegate;

  @InjectMocks Repository repository;

  @Nested
  @DisplayName("timer")
  class Timer {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.module();
        verify(delegate, times(1)).module();
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
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.indexId(0);
        verify(delegate, times(1)).indexId(0);
      }
    }
  }

  @Nested
  @DisplayName("save")
  class Save {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.save(0);
        verify(delegate, times(1)).save(0);
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
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.delete(0);
        verify(delegate, times(1)).delete(0);
      }
    }
  }

  @Nested
  @DisplayName("deleteById")
  class DeleteById {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.deleteById(0);
        verify(delegate, times(1)).deleteById(0);
      }
    }
  }

  @Nested
  @DisplayName("find")
  class Find {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.find(0);
        verify(delegate, times(1)).find(0);
      }
    }
  }

  @Nested
  @DisplayName("findAll")
  class findAll {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        repository.findAll(List.of(0));
        verify(delegate, times(1)).findAll(List.of(0));
      }
    }
  }
}
