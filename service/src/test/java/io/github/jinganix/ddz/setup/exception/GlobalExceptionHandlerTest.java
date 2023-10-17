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

package io.github.jinganix.ddz.setup.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.helper.exception.ApiException;
import io.github.jinganix.ddz.helper.exception.BusinessException;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.proto.error.ErrorMessage;
import io.github.jinganix.ddz.proto.error.ValidationErrorMessage;
import io.github.jinganix.ddz.tests.SpringIntegrationTests;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest extends SpringIntegrationTests {

  @Autowired GlobalExceptionHandler globalExceptionHandler;

  @Nested
  @DisplayName("handleValidationException")
  class HandleValidationException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        BindingResult result = new BeanPropertyBindingResult(globalExceptionHandler, "handler");
        result.addError(new FieldError("object", "field", "Error"));
        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception =
            new MethodArgumentNotValidException(parameter, result);
        ValidationErrorMessage message =
            globalExceptionHandler.handleValidationException(exception);
        assertThat(message.getErrors()).hasSize(1);
      }
    }
  }

  @Nested
  @DisplayName("handleBindException")
  class HandleBindException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        BindingResult result = new BeanPropertyBindingResult(globalExceptionHandler, "handler");
        result.addError(new FieldError("object", "field", "Error"));
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getExecutable()).thenReturn(mock(Method.class));
        WebExchangeBindException exception = new WebExchangeBindException(parameter, result);
        ErrorMessage message = globalExceptionHandler.handleBindException(exception);
        assertThat(message.getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
        assertThat(message.getMessage()).isNotEmpty();
      }
    }
  }

  @Nested
  @DisplayName("handleBusinessException")
  class HandleBusinessException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        BusinessException exception = BusinessException.of(ErrorCode.ERROR);
        ErrorMessage message = globalExceptionHandler.handleBusinessException(exception);
        assertThat(message.getCode()).isEqualTo(ErrorCode.ERROR);
      }
    }
  }

  @Nested
  @DisplayName("handleApiException")
  class HandleApiException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        ApiException exception = ApiException.of(ErrorCode.ERROR);
        ResponseEntity<?> entity = globalExceptionHandler.handleApiException(exception);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(entity.getBody())
            .usingRecursiveComparison()
            .isEqualTo(new ErrorMessage().setCode(ErrorCode.ERROR));
      }
    }
  }

  @Nested
  @DisplayName("handleAccessDeniedException")
  class HandleAccessDeniedException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        AccessDeniedException exception = new AccessDeniedException("error");
        ErrorMessage message = globalExceptionHandler.handleAccessDeniedException(exception);
        assertThat(message.getCode()).isEqualTo(ErrorCode.ERROR);
        assertThat(message.getMessage()).isEqualTo("error");
      }
    }
  }

  @Nested
  @DisplayName("handleGenericException")
  class HandleGenericException {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return")
      void thenReturn() {
        RuntimeException exception = new RuntimeException("error");
        ResponseEntity<?> entity = globalExceptionHandler.handleGenericException(exception);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody())
            .usingRecursiveComparison()
            .isEqualTo(new ErrorMessage().setCode(ErrorCode.ERROR).setMessage("error"));
      }
    }
  }
}
