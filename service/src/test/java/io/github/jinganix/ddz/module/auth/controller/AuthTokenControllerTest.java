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

package io.github.jinganix.ddz.module.auth.controller;

import static io.github.jinganix.ddz.tests.TestConst.MILLIS;
import static io.github.jinganix.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.module.auth.model.UserToken;
import io.github.jinganix.ddz.module.auth.repository.UserTokenRepository;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import io.github.jinganix.ddz.module.utils.TestHelper;
import io.github.jinganix.ddz.proto.auth.AuthTokenRequest;
import io.github.jinganix.ddz.proto.auth.AuthTokenResponse;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.proto.error.ErrorMessage;
import io.github.jinganix.ddz.tests.SpringIntegrationWithSpiedBeansTests;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("AuthController$token")
class AuthTokenControllerTest extends SpringIntegrationWithSpiedBeansTests {

  @Autowired TestHelper testHelper;

  @Autowired PlayerRepository playerRepository;

  @Autowired UserTokenRepository userTokenRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when refresh token is null")
    class WhenRefreshTokenIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        testHelper
            .request(UID_1, new AuthTokenRequest())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when token not found")
    class WhenTokenNotFound {

      @Test
      @DisplayName("then response UNAUTHORIZED")
      void thenResponseError() {
        testHelper
            .request(UID_1, new AuthTokenRequest("abcd"))
            .expectStatus()
            .isEqualTo(HttpStatus.UNAUTHORIZED)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REFRESH_TOKEN));
      }
    }

    @Nested
    @DisplayName("when player not found")
    class WhenPlayerNotFound {

      @Test
      @DisplayName("then response PLAYER_NOT_FOUND")
      void thenResponseError() {
        userTokenRepository.save(new UserToken("abc", UID_1));

        testHelper
            .request(UID_1, new AuthTokenRequest("abc"))
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.PLAYER_NOT_FOUND));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Test
    @DisplayName("then response token")
    void thenResponseToken() {
      playerRepository.save(new Player().setId(UID_1));
      userTokenRepository.save(new UserToken("abc", UID_1));

      when(tokenService.generate(anyLong(), anyString(), anyString())).thenReturn("test_token");
      when(uidGenerator.nextUid()).thenReturn(UID_1);
      when(utilsService.currentTimeMillis()).thenReturn(MILLIS);
      when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");

      testHelper
          .request(UID_1, new AuthTokenRequest("abc"))
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
    }
  }
}
