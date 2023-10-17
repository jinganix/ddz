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

package io.github.jinganix.ddz.module.auth.repository;

import io.github.jinganix.ddz.helper.repository.single.DelegateSingleValueRepository;
import io.github.jinganix.ddz.helper.repository.single.SingleCacheRepository;
import io.github.jinganix.ddz.helper.repository.single.SingleValueRepositoryMeta;
import io.github.jinganix.ddz.module.auth.model.UserCredential;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class UserCredentialRepository
    extends DelegateSingleValueRepository<UserCredential, String> {

  public UserCredentialRepository(Meta meta, Cache cache) {
    super(meta, cache);
  }

  @Component
  public static class Meta implements SingleValueRepositoryMeta<UserCredential, String> {

    @Override
    public String module() {
      return "model_user_credential";
    }

    @Override
    public String indexId(UserCredential entity) {
      return entity.getUsername();
    }
  }

  @Component
  public static class Cache extends SingleCacheRepository<UserCredential, String> {

    public Cache(Meta meta) {
      super(meta, null);
    }
  }
}
