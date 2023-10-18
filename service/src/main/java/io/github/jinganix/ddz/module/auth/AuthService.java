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

package io.github.jinganix.ddz.module.auth;

import io.github.jinganix.ddz.helper.auth.token.TokenService;
import io.github.jinganix.ddz.helper.utils.UtilsService;
import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.module.auth.model.UserToken;
import io.github.jinganix.ddz.module.auth.repository.UserTokenRepository;
import io.github.jinganix.ddz.proto.auth.AuthTokenResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service. */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final TokenService tokenService;

  private final UserTokenRepository userTokenRepository;

  private final UtilsService utilsService;

  public AuthTokenResponse createAuthTokenResponse(Long playerId) {
    GrantedRole grantedRole = GrantedRole.PLAYER;
    String accessToken =
        tokenService.generate(playerId, utilsService.uuid(true), grantedRole.getAuthorityName());
    String refreshToken = utilsService.uuid(true);

    long millis = utilsService.currentTimeMillis();
    UserToken token = new UserToken(refreshToken, playerId);
    userTokenRepository.save(token);
    Long expiresIn = millis + TimeUnit.MINUTES.toMillis(5);
    return new AuthTokenResponse(
        accessToken, expiresIn, refreshToken, "Bearer", grantedRole.getValue());
  }
}
