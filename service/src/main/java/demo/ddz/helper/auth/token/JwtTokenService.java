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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    prefix = "core",
    name = "token-type",
    havingValue = "jwt",
    matchIfMissing = true)
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

  private static final String PLAYER_ID = "pid";

  private static final String TOKEN = "tk";

  private static final String AUTHORITIES = "ats";

  @Value("${core.jwt-secret}")
  private final String jwtSecret;

  @Override
  public String generate(Long playerId, String uuid, String... authorities) {
    Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
    return JWT.create()
        .withArrayClaim(AUTHORITIES, authorities)
        .withClaim(PLAYER_ID, playerId)
        .withClaim(TOKEN, uuid)
        .withIssuedAt(new Date())
        .sign(algorithm);
  }

  public JwtToken decode(String text) {
    try {
      DecodedJWT jwt = JWT.decode(text);
      long millis = System.currentTimeMillis();
      if (jwt.getIssuedAt().getTime() < millis - TimeUnit.DAYS.toMillis(7)) {
        return JwtToken.INVALID_TOKEN;
      }
      List<String> authorities = jwt.getClaim(AUTHORITIES).asList(String.class);
      return new JwtToken(
          jwt.getClaim(PLAYER_ID).asLong(),
          jwt.getClaim(TOKEN).asString(),
          authorities == null ? Collections.emptyList() : authorities);
    } catch (Exception e) {
      log.error("token: " + text + " error: " + e.getMessage());
      return null;
    }
  }
}
