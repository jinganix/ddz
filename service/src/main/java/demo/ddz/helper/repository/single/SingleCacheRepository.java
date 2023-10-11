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
