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

package demo.ddz.module.auth.controller;

import static demo.ddz.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import demo.ddz.module.auth.Requesters;
import demo.ddz.module.player.Player;
import demo.ddz.module.utils.MessageListener;
import demo.ddz.module.utils.TestHelper;
import demo.ddz.proto.error.ErrorCode;
import demo.ddz.proto.error.ErrorMessage;
import demo.ddz.tests.SpringIntegrationWithSpiedBeansTests;
import io.rsocket.SocketAcceptor;
import io.rsocket.exceptions.RejectedSetupException;
import io.rsocket.metadata.WellKnownMimeType;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.util.MimeTypeUtils;
import reactor.test.StepVerifier;

@DisplayName("AuthController$connect")
class ConnectControllerTest extends SpringIntegrationWithSpiedBeansTests {

  @Autowired private RSocketRequester.Builder requesterBuilder;

  @Autowired private TestHelper testHelper;

  @Autowired private Requesters requesters;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when there is no token")
  class WhenThereIsNoToken {

    @Test
    @DisplayName("then fail to connect")
    void thenFailToConnect() {
      SocketAcceptor responder =
          RSocketMessageHandler.responder(RSocketStrategies.create(), new MessageListener());

      RSocketRequester requester =
          requesterBuilder
              .setupRoute("setup")
              .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
              .rsocketConnector(connector -> connector.acceptor(responder))
              .rsocketStrategies(builder -> builder.encoder(new BearerTokenAuthenticationEncoder()))
              .websocket(URI.create("ws://localhost:" + testHelper.getRsocketPort()));

      StepVerifier.create(requester.route("/test").retrieveMono(String.class))
          .expectErrorMatches(ex -> ex instanceof RejectedSetupException)
          .verify();
    }
  }

  @Nested
  @DisplayName("when token is invalid")
  class WhenTokenIsInvalid {

    @Test
    @DisplayName("then fail to connect")
    void thenFailToConnect() {
      SocketAcceptor responder =
          RSocketMessageHandler.responder(RSocketStrategies.create(), new MessageListener());

      RSocketRequester requester =
          requesterBuilder
              .setupRoute("setup")
              .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
              .setupMetadata(
                  new BearerTokenMetadata("invalid_token"),
                  MimeTypeUtils.parseMimeType(
                      WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
              .rsocketConnector(connector -> connector.acceptor(responder))
              .rsocketStrategies(builder -> builder.encoder(new BearerTokenAuthenticationEncoder()))
              .websocket(URI.create("ws://localhost:" + testHelper.getRsocketPort()));

      StepVerifier.create(requester.route("/test").retrieveMono(String.class))
          .expectErrorMatches(ex -> ex instanceof RejectedSetupException)
          .verify();
    }
  }

  @Nested
  @DisplayName("when player is not found")
  class WhenPlayerIsNotFound {
    @Test
    @DisplayName("then fail to connect")
    void thenFailToConnect() {
      RSocketRequester requester = testHelper.rsocketRequester(UID_1, new MessageListener());

      StepVerifier.create(requester.route("/test").retrieveMono(String.class))
          .expectErrorMatches(ex -> ex instanceof ClosedChannelException)
          .verify();
    }
  }

  @Nested
  @DisplayName("when error occurs")
  class WhenErrorOccurs {

    @Test
    @DisplayName("then disconnected")
    void thenDisconnected() {
      RSocketRequester prev = mock(RSocketRequester.class);
      when(prev.isDisposed()).thenReturn(true);
      requesters.put(UID_1, prev);
      doThrow(new RuntimeException()).when(playerRepository).find(any());

      RSocketRequester requester = testHelper.rsocketRequester(UID_1, new MessageListener());
      assertThat(requester.rsocketClient().connect()).isTrue();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> assertThat(requesters.getRequester(UID_1)).isNull());
    }
  }

  @Nested
  @DisplayName("when token is valid")
  class WhenTokenIsValid {

    @Test
    @DisplayName("then connect")
    void thenConnect() {
      playerRepository.save(new Player().setId(UID_1));

      RSocketRequester requester = testHelper.rsocketRequester(UID_1, new MessageListener());
      assertThat(requester.rsocketClient().connect()).isTrue();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> assertThat(requesters.getRequester(UID_1)).isNotNull());
    }
  }

  @Nested
  @DisplayName("when connected twice")
  class WhenConnectedTwice {

    @Test
    @DisplayName("then disconnect previous")
    void thenDisconnectPrevious() {
      playerRepository.save(new Player().setId(UID_1));

      MessageListener messageListener = new MessageListener();

      RSocketRequester requester1 = testHelper.rsocketRequester(UID_1, messageListener);
      assertThat(requester1.rsocketClient().connect()).isTrue();
      AtomicReference<RSocketRequester> ref = new AtomicReference<>();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(
              () -> {
                assertThat(requesters.getRequester(UID_1)).isNotNull();
                ref.set(requesters.getRequester(UID_1));
              });

      RSocketRequester requester2 = testHelper.rsocketRequester(UID_1, messageListener);
      assertThat(requester2.rsocketClient().connect()).isTrue();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> assertThat(requesters.getRequester(UID_1)).isNotEqualTo(ref.get()));

      ErrorMessage message = messageListener.getMessage(ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.FORCE_LOGOUT);
    }
  }

  @Nested
  @DisplayName("when disconnect")
  class WhenDisconnect {

    @Test
    @DisplayName("then requester is removed")
    void thenRequesterIsRemoved() {
      playerRepository.save(new Player().setId(UID_1));

      RSocketRequester requester = testHelper.rsocketRequester(UID_1, new MessageListener());

      assertThat(requester.rsocketClient().connect()).isTrue();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> assertThat(requesters.getRequester(UID_1)).isNotNull());

      requester.rsocketClient().dispose();
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> assertThat(requesters.getRequester(UID_1)).isNull());
    }
  }
}
