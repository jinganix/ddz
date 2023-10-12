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

package demo.ddz.module.auth.handler;

import demo.ddz.helper.uid.UidGenerator;
import demo.ddz.module.auth.AuthService;
import demo.ddz.module.player.Player;
import demo.ddz.module.player.PlayerRepository;
import demo.ddz.proto.auth.AuthLoginRequest;
import demo.ddz.proto.auth.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthLoginHandler {

  private final AuthService authService;

  private final PlayerRepository playerRepository;

  private final UidGenerator uidGenerator;

  public AuthTokenResponse handle(AuthLoginRequest request) {
    Long playerId = uidGenerator.nextUid();
    Player player = new Player().setId(playerId).setNickname(request.getNickname());
    playerRepository.save(player);
    return authService.createAuthTokenResponse(playerId);
  }
}
