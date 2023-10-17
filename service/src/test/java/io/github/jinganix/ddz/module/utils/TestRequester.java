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

package io.github.jinganix.ddz.module.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.proto.error.ErrorMessage;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import lombok.experimental.Delegate;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

public class TestRequester {

  @Delegate private final RSocketRequester requester;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public TestRequester(RSocketRequester requester) {
    this.requester = requester;
    objectMapper.registerSubtypes(NamedTypes.NAMED_TYPES);
  }

  public <T extends WebpbMessage> Mono<T> request(WebpbMessage request, Class<T> responseType) {
    return this.requester
        .route(request.webpbMeta().getPath())
        .data(request)
        .retrieveMono(String.class)
        .handle(
            (str, sink) -> {
              try {
                sink.next(objectMapper.readValue(str, responseType));
              } catch (JsonProcessingException e) {
                sink.error(new RuntimeException(e));
              }
            });
  }

  public Mono<ErrorMessage> error(WebpbMessage request) {
    return this.requester
        .route(request.webpbMeta().getPath())
        .data(request)
        .retrieveMono(String.class)
        .map(str -> new ErrorMessage(ErrorCode.OK, "Unexpected success: " + str))
        .onErrorResume(
            throwable -> {
              try {
                return Mono.just(
                    objectMapper.readValue(throwable.getMessage(), ErrorMessage.class));
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
