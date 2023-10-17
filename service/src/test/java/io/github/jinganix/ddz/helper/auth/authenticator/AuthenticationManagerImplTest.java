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

package io.github.jinganix.ddz.helper.auth.authenticator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@Nested
@DisplayName("AuthenticationManagerImpl")
class AuthenticationManagerImplTest {

  @Nested
  @DisplayName("when authenticator found")
  class WhenAuthenticatorFound {

    @Test
    @DisplayName("then authenticate")
    void thenAuthenticate() {
      Authenticator authenticator = Mockito.mock(Authenticator.class);
      when(authenticator.support(any())).thenReturn(true);

      Authentication authentication = mock(Authentication.class);
      when(authenticator.authenticate(any())).thenReturn(authentication);

      AuthenticationManager manager = new AuthenticationManagerImpl(List.of(authenticator));
      assertThat(manager.authenticate(authentication)).isEqualTo(authentication);
      verify(authenticator, times(1)).authenticate(authentication);
    }
  }

  @Nested
  @DisplayName("when authenticator not found")
  class WhenAuthenticatorNotFound {

    @Test
    @DisplayName("then throw error")
    void thenThrowError() {
      Authenticator authenticator = Mockito.mock(Authenticator.class);
      when(authenticator.support(any())).thenReturn(false);

      AuthenticationManager manager = new AuthenticationManagerImpl(List.of(authenticator));
      Authentication authentication = mock(Authentication.class);
      assertThatThrownBy(() -> manager.authenticate(authentication))
          .isInstanceOf(RuntimeException.class)
          .hasMessageStartingWith("Unhandled authentication: ");
    }
  }
}
