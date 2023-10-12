/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
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

package demo.ddz.module.auth;

import demo.ddz.helper.actor.VirtualThreadExecutor;
import demo.ddz.module.auth.handler.AuthLoginHandler;
import demo.ddz.module.auth.handler.AuthTokenHandler;
import demo.ddz.module.auth.handler.ConnectHandler;
import demo.ddz.proto.auth.AuthLoginRequest;
import demo.ddz.proto.auth.AuthTokenRequest;
import demo.ddz.proto.auth.AuthTokenResponse;
import demo.ddz.setup.argument.PlayerId;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthLoginHandler authLoginHandler;

  private final AuthTokenHandler authTokenHandler;

  private final ConnectHandler connectHandler;

  private final VirtualThreadExecutor virtualThreadExecutor;

  @ConnectMapping({"setup"})
  public void connect(@PlayerId Long playerId, RSocketRequester requester) {
    connectHandler.handle(playerId, requester);
  }

  @WebpbRequestMapping
  public Mono<AuthTokenResponse> login(@Valid @RequestBody AuthLoginRequest request) {
    return virtualThreadExecutor.publish(() -> authLoginHandler.handle(request));
  }

  @WebpbRequestMapping
  public Mono<AuthTokenResponse> token(@Valid @RequestBody AuthTokenRequest request) {
    return virtualThreadExecutor.publish(() -> authTokenHandler.handle(request));
  }
}
