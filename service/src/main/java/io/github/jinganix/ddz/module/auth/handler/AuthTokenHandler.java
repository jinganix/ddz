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

package io.github.jinganix.ddz.module.auth.handler;

import io.github.jinganix.ddz.helper.exception.ApiException;
import io.github.jinganix.ddz.module.auth.AuthService;
import io.github.jinganix.ddz.module.auth.model.UserToken;
import io.github.jinganix.ddz.module.auth.repository.UserTokenRepository;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import io.github.jinganix.ddz.proto.auth.AuthTokenRequest;
import io.github.jinganix.ddz.proto.auth.AuthTokenResponse;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenHandler {

  private final AuthService authService;

  private final PlayerRepository playerRepository;

  private final UserTokenRepository userTokenRepository;

  public AuthTokenResponse handle(AuthTokenRequest request) {
    UserToken token = userTokenRepository.find(request.getRefreshToken());
    if (token == null) {
      throw ApiException.of(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_REFRESH_TOKEN);
    }
    userTokenRepository.delete(token);
    Player player = playerRepository.find(token.getPlayerId());
    if (player == null) {
      throw ApiException.of(ErrorCode.PLAYER_NOT_FOUND);
    }
    return authService.createAuthTokenResponse(player.getId());
  }
}
