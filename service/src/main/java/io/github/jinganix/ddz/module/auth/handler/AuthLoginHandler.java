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
import io.github.jinganix.ddz.helper.uid.UidGenerator;
import io.github.jinganix.ddz.module.auth.AuthService;
import io.github.jinganix.ddz.module.auth.model.UserCredential;
import io.github.jinganix.ddz.module.auth.repository.UserCredentialRepository;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import io.github.jinganix.ddz.proto.auth.AuthLoginRequest;
import io.github.jinganix.ddz.proto.auth.AuthTokenResponse;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthLoginHandler {

  private final AuthService authService;

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private final PlayerRepository playerRepository;

  private final UidGenerator uidGenerator;

  private final UserCredentialRepository userCredentialRepository;

  public AuthTokenResponse handle(AuthLoginRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(username, password);
    try {
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      Long playerId = (Long) authentication.getDetails();
      return authService.createAuthTokenResponse(playerId);
    } catch (UsernameNotFoundException ex) {
      Player player = createPlayer(username, password);
      return authService.createAuthTokenResponse(player.getId());
    } catch (BadCredentialsException ex) {
      throw ApiException.of(ErrorCode.BAD_CREDENTIAL);
    }
  }

  private Player createPlayer(String username, String password) {
    long playerId = uidGenerator.nextUid();
    UserCredential credential =
        new UserCredential(username, passwordEncoder.encode(password), playerId);
    userCredentialRepository.save(credential);
    Player player = new Player().setId(playerId);
    playerRepository.save(player);
    return player;
  }
}
