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

package demo.ddz.module.auth.controller;

import static demo.ddz.tests.TestConst.MILLIS;
import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import demo.ddz.module.auth.model.GrantedRole;
import demo.ddz.module.player.Player;
import demo.ddz.module.player.PlayerRepository;
import demo.ddz.module.utils.TestHelper;
import demo.ddz.proto.auth.AuthLoginRequest;
import demo.ddz.proto.auth.AuthTokenResponse;
import demo.ddz.proto.error.ErrorCode;
import demo.ddz.proto.error.ErrorMessage;
import demo.ddz.tests.SpringIntegrationWithSpiedBeansTests;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("AuthController$login")
class AuthLoginControllerTest extends SpringIntegrationWithSpiedBeansTests {

  @Autowired TestHelper testHelper;

  @Autowired PlayerRepository playerRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when nickname is null")
    class WhenNicknameIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        testHelper
            .request(UID_1, new AuthLoginRequest(null))
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when nickname length < 3")
    class WhenNicknameLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(2);
        testHelper
            .request(UID_1, new AuthLoginRequest(randomString), GrantedRole.PLAYER.getValue())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when nickname length > 10")
    class WhenNicknameLengthGreater20 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(11);
        testHelper
            .request(UID_1, new AuthLoginRequest(randomString), GrantedRole.PLAYER.getValue())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Test
    @DisplayName("then player created")
    void thenPlayerCreated() {
      when(tokenService.generate(anyLong(), anyString(), anyString())).thenReturn("test_token");
      when(uidGenerator.nextUid()).thenReturn(UID_1);
      when(utilsService.currentTimeMillis()).thenReturn(MILLIS);
      when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");

      testHelper
          .request(UID_1, new AuthLoginRequest("abc"))
          .expectStatus()
          .isEqualTo(HttpStatus.OK)
          .expectBody(AuthTokenResponse.class)
          .consumeWith(
              result ->
                  assertThat(result.getResponseBody())
                      .usingRecursiveComparison()
                      .isEqualTo(
                          new AuthTokenResponse()
                              .setAccessToken("test_token")
                              .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5))
                              .setRefreshToken("test_uuid")
                              .setTokenType("Bearer")
                              .setScope(GrantedRole.PLAYER.getValue())));

      assertThat(playerRepository.find(UID_1))
          .usingRecursiveComparison()
          .isEqualTo(new Player().setId(UID_1).setNickname("abc"));
    }
  }
}
