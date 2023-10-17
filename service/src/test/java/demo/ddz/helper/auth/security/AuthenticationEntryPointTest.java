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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("AuthenticationEntryPoint")
class AuthenticationEntryPointTest {

  @Nested
  @DisplayName("when called")
  class WhenCalled {

    @Test
    @DisplayName("then response")
    void thenResponse() {
      AuthenticationEntryPoint entryPoint = new AuthenticationEntryPoint();
      ServerWebExchange exchange = mock(ServerWebExchange.class);

      ServerHttpResponse response = mock(ServerHttpResponse.class);
      when(exchange.getResponse()).thenReturn(response);
      when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());
      when(response.writeWith(any())).thenReturn(Mono.empty());

      StepVerifier.create(entryPoint.commence(exchange, new InvalidBearerTokenException("error")))
          .verifyComplete();
      verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
      verify(response, times(1)).writeWith(any());
    }
  }
}
