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
import io.github.jinganix.ddz.module.auth.model.UserCredential;
import io.github.jinganix.ddz.module.auth.repository.UserCredentialRepository;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import io.github.jinganix.ddz.module.utils.TestHelper;
import io.github.jinganix.ddz.proto.auth.AuthLoginRequest;
import io.github.jinganix.ddz.proto.auth.AuthTokenResponse;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.proto.error.ErrorMessage;
import io.github.jinganix.ddz.tests.SpringIntegrationWithSpiedBeansTests;
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

  @Autowired UserCredentialRepository userCredentialRepository;

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
    @DisplayName("when username is null")
    class WhenUsernameIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        testHelper
            .request(UID_1, new AuthLoginRequest(null, "aaaaaa"))
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when username length < 3")
    class WhenUsernameLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(2);
        testHelper
            .request(
                UID_1,
                new AuthLoginRequest(randomString, "aaaaaa"),
                GrantedRole.PLAYER.getAuthorityName())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when username length > 20")
    class WhenUsernameLengthGreater20 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(21);
        testHelper
            .request(
                UID_1,
                new AuthLoginRequest(randomString, "aaaaaa"),
                GrantedRole.PLAYER.getAuthorityName())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password is null")
    class WhenPasswordIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        testHelper
            .request(UID_1, new AuthLoginRequest("aaa", null))
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password length < 6")
    class WhenPasswordLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(5);
        testHelper
            .request(
                UID_1,
                new AuthLoginRequest("aaa", randomString),
                GrantedRole.PLAYER.getAuthorityName())
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password length > 20")
    class WhenPasswordLengthGreater20 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() {
        String randomString = RandomStringUtils.randomAlphabetic(21);
        testHelper
            .request(
                UID_1,
                new AuthLoginRequest("aaa", randomString),
                GrantedRole.PLAYER.getAuthorityName())
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
    @DisplayName("when password not match")
    class WhenPasswordNotMatch {

      @Test
      @DisplayName("then response BAD_CREDENTIAL")
      void thenResponseError() {
        userCredentialRepository.save(new UserCredential("abc", "abcdef", UID_1));

        testHelper
            .request(UID_1, new AuthLoginRequest("abc", "aaaaaa"))
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody(ErrorMessage.class)
            .value(e -> assertThat(e.getCode()).isEqualTo(ErrorCode.BAD_CREDENTIAL));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Nested
    @DisplayName("when username not found")
    class WhenUsernameNotFound {

      @Test
      @DisplayName("then player created")
      void thenPlayerCreated() {
        when(tokenService.generate(anyLong(), anyString(), anyString())).thenReturn("test_token");
        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
        when(utilsService.currentTimeMillis()).thenReturn(MILLIS);

        testHelper
            .request(UID_1, new AuthLoginRequest("abc", "aaaaaa"))
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
            .isEqualTo(new Player().setId(UID_1));

        UserCredential userCredential = userCredentialRepository.find("abc");
        assertThat(userCredential.getUsername()).isEqualTo("abc");
        assertThat(passwordEncoder.matches("aaaaaa", userCredential.getPassword())).isTrue();
        assertThat(userCredential.getPlayerId()).isEqualTo(UID_1);
      }
    }

    @Nested
    @DisplayName("when player not found")
    class WhenPlayerNotFound {

      @Test
      @DisplayName("then response")
      void thenResponse() {
        userCredentialRepository.save(
            new UserCredential("abc", passwordEncoder.encode("aaaaaa"), UID_1));
        when(tokenService.generate(anyLong(), anyString(), anyString())).thenReturn("test_token");
        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
        when(utilsService.currentTimeMillis()).thenReturn(MILLIS);

        testHelper
            .request(UID_1, new AuthLoginRequest("abc", "aaaaaa"))
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
            .isEqualTo(new Player().setId(UID_1));

        UserCredential userCredential = userCredentialRepository.find("abc");
        assertThat(userCredential.getUsername()).isEqualTo("abc");
        assertThat(passwordEncoder.matches("aaaaaa", userCredential.getPassword())).isTrue();
        assertThat(userCredential.getPlayerId()).isEqualTo(UID_1);
      }
    }

    @Nested
    @DisplayName("when username found")
    class WhenUsernameFound {

      @Test
      @DisplayName("then response")
      void thenResponse() {
        playerRepository.save(new Player().setId(UID_1));
        userCredentialRepository.save(
            new UserCredential("abc", passwordEncoder.encode("aaaaaa"), UID_1));
        when(tokenService.generate(anyLong(), anyString(), anyString())).thenReturn("test_token");
        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
        when(utilsService.currentTimeMillis()).thenReturn(MILLIS);

        testHelper
            .request(UID_1, new AuthLoginRequest("abc", "aaaaaa"))
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
            .isEqualTo(new Player().setId(UID_1));

        UserCredential userCredential = userCredentialRepository.find("abc");
        assertThat(userCredential.getUsername()).isEqualTo("abc");
        assertThat(passwordEncoder.matches("aaaaaa", userCredential.getPassword())).isTrue();
        assertThat(userCredential.getPlayerId()).isEqualTo(UID_1);
      }
    }
  }
}
