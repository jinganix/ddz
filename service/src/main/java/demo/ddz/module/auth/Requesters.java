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

package demo.ddz.module.auth;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.utils.ErrorCode;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbMeta;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

@Component
public class Requesters {

  private final Map<Long, RSocketRequester> requesterMap = new ConcurrentHashMap<>();

  public RSocketRequester put(Long playerId, RSocketRequester requester) {
    return requesterMap.put(playerId, requester);
  }

  public void remove(Long playerId, RSocketRequester requester) {
    RSocketRequester prev = requesterMap.get(playerId);
    if (Objects.equals(prev, requester)) {
      requesterMap.remove(playerId);
    }
  }

  public RSocketRequester getRequester(Long playerId) {
    return this.requesterMap.get(playerId);
  }

  public void fireAndForget(Long playerId, WebpbMessage message) {
    RSocketRequester requester = requesterMap.get(playerId);
    if (requester == null) {
      return;
    }
    WebpbMeta meta = message.webpbMeta();
    requester.route(meta.getPath()).data(message).send().block();
  }

  public void broadcast(WebpbMessage message) {
    WebpbMeta meta = message.webpbMeta();
    Flux.fromIterable(requesterMap.values())
        .flatMap(requester -> requester.route(meta.getPath()).data(message).send())
        .blockLast();
  }

  public void multicast(Collection<Long> playerIds, WebpbMessage message) {
    if (CollectionUtils.isEmpty(playerIds)) {
      return;
    }
    WebpbMeta meta = message.webpbMeta();
    Flux.fromStream(playerIds.stream().map(requesterMap::get).filter(Objects::nonNull))
        .flatMap(requester -> requester.route(meta.getPath()).data(message).send())
        .blockLast();
  }

  public <R extends WebpbMessage> R requestResponse(
      Long playerId, WebpbMessage message, Class<R> responseType) {
    RSocketRequester requester = requesterMap.get(playerId);
    if (requester == null) {
      throw BusinessException.of(ErrorCode.PLAYER_IS_OFFLINE);
    }
    WebpbMeta meta = message.webpbMeta();
    return requester.route(meta.getPath()).data(message).retrieveMono(responseType).block();
  }
}
