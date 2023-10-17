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

package demo.ddz.helper.auth.token.authenticator;

import demo.ddz.helper.auth.authenticator.Authenticator;
import demo.ddz.helper.auth.token.JwtToken;
import demo.ddz.helper.auth.token.JwtTokenService;
import demo.ddz.helper.auth.token.PlayerAuthenticationToken;
import demo.ddz.module.auth.model.GrantedRole;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticator implements Authenticator {

  private final JwtTokenService jwtTokenService;

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof BearerTokenAuthenticationToken;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    String token = ((BearerTokenAuthenticationToken) authentication).getToken();
    JwtToken jwt = jwtTokenService.decode(token);
    if (jwt == null) {
      throw new InvalidBearerTokenException("Invalid bearer token");
    }
    return new PlayerAuthenticationToken(
        jwt.getPlayerId(),
        jwt.getAuthorities().stream()
            .map(v -> GrantedRole.fromValue(v).getAuthority())
            .collect(Collectors.toSet()));
  }
}
