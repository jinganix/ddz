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

import demo.ddz.module.auth.Requesters;
import demo.ddz.module.player.Player;
import demo.ddz.module.player.PlayerRepository;
import demo.ddz.proto.error.ErrorCode;
import demo.ddz.proto.error.ErrorMessage;
import demo.ddz.setup.argument.PlayerId;
import io.github.jinganix.webpb.runtime.WebpbMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectHandler {

  private final PlayerRepository playerRepository;

  private final Requesters requesters;

  public void handle(@PlayerId Long playerId, RSocketRequester requester) {
    RSocketRequester replaced = this.requesters.put(playerId, requester);
    if (replaced != null && !replaced.isDisposed()) {
      ErrorMessage message = new ErrorMessage().setCode(ErrorCode.FORCE_LOGOUT);
      WebpbMeta meta = message.webpbMeta();
      replaced
          .route(meta.getPath())
          .data(message)
          .send()
          .doFinally(ignore -> replaced.dispose())
          .subscribe();
    }
    requester
        .rsocket()
        .onClose()
        .doFirst(
            () -> {
              log.info("CONNECTED playerId {}", playerId);
              Player player = playerRepository.find(playerId);
              if (player == null) {
                log.warn("DISCONNECTED playerId {}, player not found", playerId);
                requester.dispose();
              }
            })
        .doOnError(error -> log.warn("Connection error playerId: " + playerId, error))
        .doFinally(
            signalType -> {
              log.info("DISCONNECTED playerId {}, signalType: {}", playerId, signalType);
              this.requesters.remove(playerId, requester);
            })
        .subscribe();
  }
}
