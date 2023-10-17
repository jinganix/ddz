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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.helper.actor.VirtualThreadExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReactiveAuthenticationManagerImpl")
class ReactiveAuthenticationManagerImplTest {

  @Mock AuthenticationManager authenticationManager;

  @Mock VirtualThreadExecutor virtualThreadExecutor;

  @InjectMocks ReactiveAuthenticationManagerImpl reactiveAuthenticationManager;

  @Nested
  @DisplayName("authenticate")
  class Authenticate {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then authenticate")
      void thenAuthenticate() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        when(virtualThreadExecutor.publish(any()))
            .thenAnswer(
                (Answer<Mono<Authentication>>)
                    invocation -> Mono.just(authenticationManager.authenticate(authentication)));

        StepVerifier.create(reactiveAuthenticationManager.authenticate(authentication))
            .expectNext(authentication)
            .verifyComplete();

        verify(authenticationManager, times(1)).authenticate(authentication);
        verify(virtualThreadExecutor, times(1)).publish(any());
      }
    }
  }
}
