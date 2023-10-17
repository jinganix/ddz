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

package demo.ddz.helper.auth.token;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("JwtTokenService")
class JwtTokenServiceTest {

  JwtTokenService jwtTokenService = new JwtTokenService("test_secret");

  @Nested
  @DisplayName("generate")
  class Generate {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then generate")
      void thenGenerate() {
        assertThat(jwtTokenService.generate(UID_1, "test_uid", "player")).isNotEmpty();
      }
    }
  }

  @Nested
  @DisplayName("decode")
  class Decode {

    @Nested
    @DisplayName("when token is invalid")
    class WhenTokenIsInvalid {

      @Test
      @DisplayName("then return null")
      void thenReturnNull() {
        assertThat(jwtTokenService.decode("test")).isNull();
      }
    }

    @Nested
    @DisplayName("when token is expired")
    class WhenTokenIsExpired {

      @Test
      @DisplayName("then return invalid token")
      void thenReturnInvalidToken() {
        String text =
            JWT.create()
                .withIssuedAt(LocalDate.of(2000, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .sign(Algorithm.HMAC256("test_secret"));
        assertThat(jwtTokenService.decode(text)).isEqualTo(JwtToken.INVALID_TOKEN);
      }
    }

    @Nested
    @DisplayName("when token is invalid")
    class WhenTokenIsValid {

      @Test
      @DisplayName("then return invalid token")
      void thenReturnInvalidToken() {
        String text =
            JWT.create()
                .withIssuedAt(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC))
                .withClaim("pid", UID_1)
                .withClaim("tk", "test_uuid")
                .sign(Algorithm.HMAC256("test_secret"));
        JwtToken token = jwtTokenService.decode(text);
        assertThat(token.getAuthorities()).isEmpty();
        assertThat(token.getPlayerId()).isEqualTo(UID_1);
        assertThat(token.getUuid()).isEqualTo("test_uuid");
      }
    }
  }
}
