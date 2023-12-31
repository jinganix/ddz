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

import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.proto.auth.AuthLoginRequest;
import io.github.jinganix.ddz.proto.auth.AuthTokenRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;

@Configuration
public class AuthCustomizeConfiguration {

  @Bean
  Customizer<AuthorizeExchangeSpec> authorizeExchangeSpecCustomizer() {
    return spec -> {
      spec.pathMatchers(AuthLoginRequest.WEBPB_METHOD, AuthLoginRequest.WEBPB_PATH).permitAll();
      spec.pathMatchers(AuthTokenRequest.WEBPB_METHOD, AuthTokenRequest.WEBPB_PATH).permitAll();
      spec.anyExchange().authenticated();
    };
  }

  @Bean
  Customizer<AuthorizePayloadsSpec> authorizePayloadsSpecCustomizer() {
    return spec -> {
      spec.setup().hasRole(GrantedRole.PLAYER.getValue());
      spec.anyExchange().authenticated();
    };
  }
}
