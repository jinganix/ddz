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

package io.github.jinganix.ddz.module.auth.authenticator;

import io.github.jinganix.ddz.helper.auth.authenticator.Authenticator;
import io.github.jinganix.ddz.helper.auth.token.PlayerAuthenticationToken;
import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.module.auth.model.UserCredential;
import io.github.jinganix.ddz.module.auth.repository.UserCredentialRepository;
import io.github.jinganix.ddz.module.player.Player;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsAuthenticator implements Authenticator {

  private final PasswordEncoder passwordEncoder;

  private final UserCredentialRepository userCredentialRepository;

  private final PlayerRepository playerRepository;

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof UsernamePasswordAuthenticationToken;
  }

  @Override
  public PlayerAuthenticationToken authenticate(Authentication authentication) {
    String username = authentication.getPrincipal().toString();
    String password = authentication.getCredentials().toString();
    UserCredential credential = userCredentialRepository.find(username);
    if (credential == null) {
      throw new UsernameNotFoundException("Invalid username");
    }
    if (!passwordEncoder.matches(password, credential.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }
    Player player = playerRepository.find(credential.getPlayerId());
    if (player == null) {
      throw new UsernameNotFoundException("Invalid username");
    }
    return new PlayerAuthenticationToken(player.getId(), Set.of(GrantedRole.PLAYER.getAuthority()));
  }
}
