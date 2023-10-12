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

package demo.ddz.helper.auth.token;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("PlayerAuthenticationToken")
class PlayerAuthenticationTokenTest {

  PlayerAuthenticationToken token =
      new PlayerAuthenticationToken(UID_1, Set.of(new SimpleGrantedAuthority("A")));

  @Nested
  @DisplayName("getAuthorities")
  class GetAuthorities {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.getAuthorities()).isEqualTo(Set.of(new SimpleGrantedAuthority("A")));
      }
    }
  }

  @Nested
  @DisplayName("getCredentials")
  class GetCredentials {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.getCredentials()).isEqualTo(UID_1);
      }
    }
  }

  @Nested
  @DisplayName("getDetails")
  class GetDetails {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.getDetails()).isEqualTo(UID_1);
      }
    }
  }

  @Nested
  @DisplayName("getPrincipal")
  class GetPrincipal {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.getPrincipal()).isEqualTo(UID_1);
      }
    }
  }

  @Nested
  @DisplayName("isAuthenticated")
  class IsAuthenticated {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.isAuthenticated()).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("setAuthenticated")
  class SetAuthenticated {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        token.setAuthenticated(false);
        assertThat(token.isAuthenticated()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("getName")
  class GetName {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        assertThat(token.getName()).isNull();
      }
    }
  }
}
