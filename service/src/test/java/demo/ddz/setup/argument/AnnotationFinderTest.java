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

package demo.ddz.setup.argument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;

@DisplayName("AnnotationFinder")
class AnnotationFinderTest {

  @Nested
  @DisplayName("findMethodAnnotation")
  class FindMethodAnnotation {

    Nested obj =
        new Nested() {
          @Override
          public Class<? extends Annotation> annotationType() {
            return Nested.class;
          }
        };

    @Nested
    @DisplayName("when parameter has annotation")
    class WhenParameterHasAnnotation {

      @Test
      @DisplayName("then return the annotation")
      void thenReturnTheAnnotation() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getParameterAnnotation(Nested.class)).thenReturn(obj);

        assertThat(AnnotationFinder.findMethodAnnotation(Nested.class, parameter)).isEqualTo(obj);
      }
    }

    @Nested
    @DisplayName("when parameter has parameter annotations")
    class WhenParameterHasParameterAnnotations {

      @Test
      @DisplayName("then return the annotation")
      void thenReturnTheAnnotation() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getParameterAnnotations()).thenReturn(new Annotation[] {obj});

        try (MockedStatic<AnnotationUtils> annotationUtils = mockStatic(AnnotationUtils.class)) {
          annotationUtils
              .when(() -> AnnotationUtils.findAnnotation(any(Class.class), any()))
              .thenReturn(obj);
          assertThat(AnnotationFinder.findMethodAnnotation(Nested.class, parameter)).isEqualTo(obj);
        }
      }
    }

    @Nested
    @DisplayName("when parameter has no matched parameter annotations")
    class WhenParameterHasNoMatchedParameterAnnotations {

      @Test
      @DisplayName("then return null")
      void thenReturnNull() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getParameterAnnotations()).thenReturn(new Annotation[] {obj});

        try (MockedStatic<AnnotationUtils> annotationUtils = mockStatic(AnnotationUtils.class)) {
          annotationUtils
              .when(() -> AnnotationUtils.findAnnotation(any(Class.class), any()))
              .thenReturn(null);
          assertThat(AnnotationFinder.findMethodAnnotation(Nested.class, parameter)).isNull();
        }
      }
    }
  }
}
