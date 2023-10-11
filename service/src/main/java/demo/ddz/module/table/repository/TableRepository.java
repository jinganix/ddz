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

package demo.ddz.module.table.repository;

import demo.ddz.helper.repository.single.DelegateSingleValueRepository;
import demo.ddz.helper.repository.single.SingleCacheRepository;
import demo.ddz.helper.repository.single.SingleValueRepositoryMeta;
import demo.ddz.module.table.Table;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class TableRepository extends DelegateSingleValueRepository<Table, Long> {

  public TableRepository(Meta meta, Cache cache) {
    super(meta, cache);
  }

  @Component
  public static class Meta implements SingleValueRepositoryMeta<Table, Long> {

    @Override
    public String module() {
      return "model_table";
    }

    @Override
    public Long indexId(Table entity) {
      return entity.getId();
    }
  }

  @Component
  public static class Cache extends SingleCacheRepository<Table, Long> {

    public Cache(Meta meta) {
      super(meta, null);
    }
  }
}
