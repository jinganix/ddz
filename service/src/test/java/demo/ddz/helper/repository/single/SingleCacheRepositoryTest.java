package demo.ddz.helper.repository.single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SingleCacheRepository")
class SingleCacheRepositoryTest {

  static class Entity {
    Integer id;

    Entity(Integer id) {
      this.id = id;
    }
  }

  SingleValueRepositoryMeta<Entity, Integer> meta =
      new SingleValueRepositoryMeta<>() {
        @Override
        public String module() {
          return "test";
        }

        @Override
        public Integer indexId(Entity entity) {
          return entity.id;
        }
      };

  @Mock SingleValueRepository<Entity, Integer> delegate;

  Map<Integer, Entity> cache = new HashMap<>();

  SingleCacheRepository<Entity, Integer> repository;

  @BeforeEach
  void setup() {
    repository = new SingleCacheRepository<>(meta, delegate, cache);
    cache.clear();
  }

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then concrete")
      void thenConcrete() {
        SingleCacheRepository<Entity, Integer> repository =
            new SingleCacheRepository<>(meta, delegate);
        assertThat(repository.cache).isNotNull();
      }
    }
  }

  @Nested
  @DisplayName("save")
  class Save {

    @Nested
    @DisplayName("when save entity")
    class WhenSaveEntity {

      @Test
      @DisplayName("then saved")
      void thenSaved() {
        Entity entity = new Entity(1);
        repository.save(entity);
        assertThat(cache.get(1)).isEqualTo(entity);
        verify(delegate, times(1)).save(entity);
      }
    }

    @Nested
    @DisplayName("when delegate is null")
    class WhenDelegateIsNull {

      @Test
      @DisplayName("then delegate not called")
      void thenDelegateNotCalled() {
        SingleCacheRepository<Entity, Integer> repository =
            new SingleCacheRepository<>(meta, null, cache);
        Entity entity = new Entity(1);
        repository.save(entity);
        assertThat(cache.get(1)).isEqualTo(entity);
        verify(delegate, never()).save(entity);
      }
    }
  }

  @Nested
  @DisplayName("deleteById")
  class DeleteById {

    @Nested
    @DisplayName("when entity exists")
    class WhenEntityExists {

      @Test
      @DisplayName("then return 1")
      void thenReturn1() {
        Entity entity = new Entity(1);
        cache.put(1, entity);
        assertThat(repository.deleteById(1)).isEqualTo(1);
        assertThat(cache.get(1)).isNull();
        verify(delegate, times(1)).deleteById(1);
      }
    }

    @Nested
    @DisplayName("when entity not exists")
    class WhenEntityNotExists {

      @Test
      @DisplayName("then return 0")
      void thenReturn0() {
        assertThat(repository.deleteById(1)).isEqualTo(0);
        assertThat(cache.get(1)).isNull();
        verify(delegate, times(1)).deleteById(1);
      }
    }

    @Nested
    @DisplayName("when delegate is null")
    class WhenDelegateIsNull {

      @Test
      @DisplayName("then delegate not called")
      void thenDelegateNotCalled() {
        SingleCacheRepository<Entity, Integer> repository =
            new SingleCacheRepository<>(meta, null, cache);
        repository.deleteById(1);
        assertThat(cache.get(1)).isNull();
        verify(delegate, never()).deleteById(1);
      }
    }
  }

  @Nested
  @DisplayName("find")
  class Find {

    @Nested
    @DisplayName("when entity exists")
    class WhenEntityExists {

      @Test
      @DisplayName("then return entity")
      void thenReturnEntity() {
        Entity entity = new Entity(1);
        cache.put(1, entity);
        assertThat(repository.find(1)).isEqualTo(entity);
        verify(delegate, never()).find(1);
      }
    }

    @Nested
    @DisplayName("when entity not exists")
    class WhenEntityNotExists {

      @Test
      @DisplayName("then return null")
      void thenReturnNull() {
        assertThat(repository.find(1)).isNull();
        verify(delegate, times(1)).find(1);
      }
    }

    @Nested
    @DisplayName("when delegate is null")
    class WhenDelegateIsNull {

      @Test
      @DisplayName("then delegate not called")
      void thenDelegateNotCalled() {
        SingleCacheRepository<Entity, Integer> repository =
            new SingleCacheRepository<>(meta, null, cache);
        repository.find(1);
        verify(delegate, never()).find(1);
      }
    }
  }

  @Nested
  @DisplayName("findAll")
  class FindAll {

    @Nested
    @DisplayName("when all ids cached")
    class WhenAllIdsCached {

      @Test
      @DisplayName("then return entities")
      void thenReturnEntities() {
        Entity entity = new Entity(1);
        cache.put(1, entity);
        assertThat(repository.findAll(List.of(1))).containsExactly(entity);
        verify(delegate, never()).findAll(List.of(1));
      }
    }

    @Nested
    @DisplayName("when some ids not cached")
    class WhenSomeIdsNotCached {

      @Test
      @DisplayName("then load entities")
      void thenLoadEntities() {
        when(delegate.findAll(List.of(2))).thenReturn(List.of(new Entity(2)));
        Entity entity = new Entity(1);
        cache.put(1, entity);
        assertThat(repository.findAll(List.of(1, 2)))
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(entity, new Entity(2));
        verify(delegate, times(1)).findAll(List.of(2));
      }
    }

    @Nested
    @DisplayName("when delegate is null")
    class WhenDelegateIsNull {

      @Test
      @DisplayName("then delegate not called")
      void thenDelegateNotCalled() {
        SingleCacheRepository<Entity, Integer> repository =
            new SingleCacheRepository<>(meta, null, cache);
        repository.findAll(List.of(1));
        verify(delegate, never()).find(1);
      }
    }
  }
}
