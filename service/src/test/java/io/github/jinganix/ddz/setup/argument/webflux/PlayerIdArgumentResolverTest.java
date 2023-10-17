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

package io.github.jinganix.ddz.setup.argument.webflux;

import static io.github.jinganix.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.setup.argument.AnnotationFinder;
import io.github.jinganix.ddz.setup.argument.PlayerId;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("PlayerIdArgumentResolver")
class PlayerIdArgumentResolverTest {

  @Nested
  @DisplayName("supportsParameter")
  class SupportsParameter {

    @Nested
    @DisplayName("when annotation is null")
    class WhenAnnotationIsNull {

      @Test
      @DisplayName("then return false")
      void thenReturnFalse() {
        try (MockedStatic<AnnotationFinder> annotationUtils = mockStatic(AnnotationFinder.class)) {
          MethodParameter parameter = mock(MethodParameter.class);
          annotationUtils
              .when(() -> AnnotationFinder.findMethodAnnotation(PlayerId.class, parameter))
              .thenReturn(null);
          PlayerIdArgumentResolver resolver = new PlayerIdArgumentResolver();
          assertThat(resolver.supportsParameter(parameter)).isFalse();
        }
      }
    }

    @Nested
    @DisplayName("when annotation is not null")
    class WhenAnnotationIsNotNull {

      @Test
      @DisplayName("then return true")
      void thenReturnTrue() {
        Nested obj =
            new Nested() {
              @Override
              public Class<? extends Annotation> annotationType() {
                return Nested.class;
              }
            };
        try (MockedStatic<AnnotationFinder> annotationUtils = mockStatic(AnnotationFinder.class)) {
          MethodParameter parameter = mock(MethodParameter.class);
          annotationUtils
              .when(() -> AnnotationFinder.findMethodAnnotation(PlayerId.class, parameter))
              .thenReturn(obj);
          PlayerIdArgumentResolver resolver = new PlayerIdArgumentResolver();
          assertThat(resolver.supportsParameter(parameter)).isTrue();
        }
      }
    }
  }

  @Nested
  @DisplayName("resolveArgument")
  class ResolveArgument {

    @Nested
    @DisplayName("when adaptor is null")
    class WhenAdaptorIsNull {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        try (MockedStatic<ReactiveSecurityContextHolder> holder =
            mockStatic(ReactiveSecurityContextHolder.class)) {
          SecurityContext context = mock(SecurityContext.class);
          Authentication authentication = mock(Authentication.class);
          when(context.getAuthentication()).thenReturn(authentication);
          when(authentication.getDetails()).thenReturn(UID_1);
          holder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(context));
          PlayerIdArgumentResolver resolver = new PlayerIdArgumentResolver();
          MethodParameter parameter = mock(MethodParameter.class);
          StepVerifier.create(
                  resolver.resolveArgument(
                      parameter, mock(BindingContext.class), mock(ServerWebExchange.class)))
              .expectNext(UID_1)
              .verifyComplete();
        }
      }
    }
  }
}
