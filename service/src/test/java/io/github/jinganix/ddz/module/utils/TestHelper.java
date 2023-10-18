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

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.helper.auth.token.TokenService;
import io.github.jinganix.ddz.helper.repository.single.SingleCacheRepository;
import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import java.net.URI;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.rsocket.context.RSocketServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@Service
public class TestHelper implements ApplicationContextAware {

  @Getter private final MessageListener listener = new MessageListener();

  @Autowired(required = false)
  protected WebTestClient webTestClient;

  private ApplicationContext context;

  @Value("${server.servlet.context-path?:}")
  private String contextPath;

  @Autowired private TokenService tokenService;

  @Autowired private RSocketRequester.Builder requesterBuilder;

  @Getter private int rsocketPort;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }

  @EventListener
  public void onServerInitialized(RSocketServerInitializedEvent event) {
    rsocketPort = event.getServer().address().getPort();
  }

  public void clearAll() {
    clearCache();
    listener.clear();
  }

  public void clearCache() {
    this.context
        .getBeansOfType(SingleCacheRepository.class)
        .forEach((s, repository) -> repository.clearCache());
  }

  public WebTestClient.ResponseSpec request(
      Long playerId, WebpbMessage message, String... authorities) {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    String token = tokenService.generate(playerId, UUID.randomUUID().toString(), authorities);
    return webTestClient
        .method(method)
        .uri(contextPath == null ? "" : contextPath + WebpbUtils.formatUrl(message))
        .headers(http -> http.setBearerAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(WebpbUtils.serialize(message))
        .exchange();
  }

  public RSocketRequester rsocketRequester(Long playerId, MessageListener messageListener) {
    String token =
        tokenService.generate(
            playerId, UUID.randomUUID().toString(), GrantedRole.PLAYER.getAuthorityName());

    SocketAcceptor responder =
        RSocketMessageHandler.responder(RSocketStrategies.create(), messageListener);

    return requesterBuilder
        .setupRoute("setup")
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
        .setupMetadata(
            new BearerTokenMetadata(token),
            MimeTypeUtils.parseMimeType(
                WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
        .rsocketConnector(connector -> connector.acceptor(responder))
        .rsocketStrategies(builder -> builder.encoder(new BearerTokenAuthenticationEncoder()))
        .websocket(URI.create("ws://localhost:" + rsocketPort));
  }

  public TestRequester createRequester(Long playerId, MessageListener messageListener) {
    return new TestRequester(rsocketRequester(playerId, messageListener));
  }

  public MessageListener listen(Long playerId) {
    MessageListener messageListener = new MessageListener();
    createRequester(playerId, messageListener).rsocketClient().connect();
    return messageListener;
  }

  public <T extends WebpbMessage> Mono<T> rsocket(
      Long playerId, WebpbMessage request, Class<T> responseType) {
    return createRequester(playerId, listener).request(request, responseType);
  }

  public void expectError(Long playerId, ErrorCode errorCode, WebpbMessage request) {
    StepVerifier.create(createRequester(playerId, listener).error(request))
        .consumeNextWith(message -> assertThat(message.getCode()).isEqualTo(errorCode))
        .verifyComplete();
  }

  public <T extends WebpbMessage> void expectSuccess(
      Long playerId, WebpbMessage request, Class<T> responseType) {
    StepVerifier.create(createRequester(playerId, listener).request(request, responseType))
        .consumeNextWith(message -> assertThat(message).isNotNull())
        .verifyComplete();
  }

  public <T extends WebpbMessage> T getMessage(Class<T> type) {
    return listener.getMessage(type);
  }
}
