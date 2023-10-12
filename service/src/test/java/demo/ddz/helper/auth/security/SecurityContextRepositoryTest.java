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

package demo.ddz.helper.auth.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityContextRepository")
class SecurityContextRepositoryTest {

  @Mock ReactiveAuthenticationManager reactiveAuthenticationManager;

  @InjectMocks SecurityContextRepository securityContextRepository;

  @Nested
  @DisplayName("save")
  class Save {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then throw error")
      void thenThrowError() {
        StepVerifier.create(
                Mono.defer(
                    () ->
                        securityContextRepository.save(
                            mock(ServerWebExchange.class), mock(SecurityContext.class))))
            .expectErrorMatches(
                throwable ->
                    throwable instanceof UnsupportedOperationException
                        && throwable.getMessage().equals("Not supported yet."))
            .verify();
      }
    }
  }

  @Nested
  @DisplayName("load")
  class Load {

    @Nested
    @DisplayName("when header is null")
    class WhenHeaderIsNull {

      @Test
      @DisplayName("then return empty")
      void thenReturnEmpty() {

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        StepVerifier.create(securityContextRepository.load(exchange)).verifyComplete();
        verify(reactiveAuthenticationManager, never()).authenticate(any());
      }
    }

    @Nested
    @DisplayName("when header not start with Bearer")
    class WhenHeaderNotStartWithBearer {

      @Test
      @DisplayName("then return empty")
      void thenReturnEmpty() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "abc");
        when(request.getHeaders()).thenReturn(headers);
        StepVerifier.create(securityContextRepository.load(exchange)).verifyComplete();
        verify(reactiveAuthenticationManager, never()).authenticate(any());
      }
    }

    @Nested
    @DisplayName("when header start with Bearer")
    class WhenHeaderStartWithBearer {

      @Test
      @DisplayName("then return empty")
      void thenReturnEmpty() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer abc");
        when(request.getHeaders()).thenReturn(headers);
        when(reactiveAuthenticationManager.authenticate(any()))
            .thenReturn(Mono.just(mock(Authentication.class)));

        StepVerifier.create(securityContextRepository.load(exchange))
            .expectNextMatches(Objects::nonNull)
            .verifyComplete();
        verify(reactiveAuthenticationManager, times(1)).authenticate(any());
      }
    }
  }
}
