package demo.ddz.helper.repository.single;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleCacheRepository<T, ID> extends AbstractSingleValueRepository<T, ID> {

  protected final Map<ID, T> cache;

  public SingleCacheRepository(
      SingleValueRepositoryMeta<T, ID> meta, SingleValueRepository<T, ID> delegate) {
    super(meta, delegate);

    Cache<ID, T> cache =
        Caffeine.newBuilder().maximumSize(20000).expireAfterAccess(Duration.ofMinutes(5)).build();
    this.cache = cache.asMap();
  }

  public SingleCacheRepository(
      SingleValueRepositoryMeta<T, ID> meta,
      SingleValueRepository<T, ID> delegate,
      Map<ID, T> cache) {
    super(meta, delegate);
    this.cache = cache;
  }

  @Override
  public T save(T entity) {
    cache.put(indexId(entity), entity);
    if (delegate != null) {
      delegate.save(entity);
    }
    return entity;
  }

  @Override
  public Integer deleteById(ID id) {
    Integer value = cache.remove(id) == null ? 0 : 1;
    if (delegate != null) {
      delegate.deleteById(id);
    }
    return value;
  }

  @Override
  public T find(ID id) {
    T value = cache.get(id);
    if (delegate != null && value == null) {
      return delegate.find(id);
    }
    return value;
  }

  @Override
  public List<T> findAll(List<ID> ids) {
    List<T> values = new ArrayList<>();
    List<ID> uncachedIds = new ArrayList<>();
    for (ID id : ids) {
      T data = cache.get(id);
      if (data == null) {
        uncachedIds.add(id);
      } else {
        values.add(data);
      }
    }
    if (delegate != null && !uncachedIds.isEmpty()) {
      values.addAll(delegate.findAll(uncachedIds));
    }
    return values;
  }
}
