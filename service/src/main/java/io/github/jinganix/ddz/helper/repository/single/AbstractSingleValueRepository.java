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

package io.github.jinganix.ddz.helper.repository.single;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSingleValueRepository<T, ID> implements SingleValueRepository<T, ID> {

  protected final SingleValueRepositoryMeta<T, ID> meta;

  protected final SingleValueRepository<T, ID> delegate;

  @Override
  public String module() {
    return meta.module();
  }

  @Override
  public ID indexId(T entity) {
    return meta.indexId(entity);
  }

  @Override
  public Integer delete(T entity) {
    return this.deleteById(indexId(entity));
  }
}
