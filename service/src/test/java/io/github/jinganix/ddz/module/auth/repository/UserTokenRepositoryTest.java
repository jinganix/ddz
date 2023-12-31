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

import static io.github.jinganix.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.module.auth.model.UserToken;
import io.github.jinganix.ddz.tests.SpringIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserTokenRepository")
class UserTokenRepositoryTest {

  @Nested
  @DisplayName("Meta")
  class Meta extends SpringIntegrationTests {

    @Autowired UserTokenRepository.Meta meta;

    @Nested
    @DisplayName("module")
    class Module {

      @Nested
      @DisplayName("when called")
      class WhenCalled {

        @Test
        @DisplayName("then return")
        void thenReturn() {
          assertThat(meta.module()).isEqualTo("model_user_token");
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
          assertThat(meta.indexId(new UserToken("token", UID_1))).isEqualTo("token");
        }
      }
    }
  }
}
