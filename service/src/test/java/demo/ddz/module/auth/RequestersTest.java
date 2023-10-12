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

import static demo.ddz.tests.TestConst.UID_1;
import static demo.ddz.tests.TestConst.UID_2;
import static demo.ddz.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import demo.ddz.helper.exception.BusinessException;
import demo.ddz.module.utils.ErrorCode;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbMeta;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester.RequestSpec;
import reactor.core.publisher.Mono;

@DisplayName("Requesters")
class RequestersTest {

  RSocketRequester mockRequester() {
    RSocketRequester requester = mock(RSocketRequester.class);
    RequestSpec spec = mock(RequestSpec.class);
    when(requester.route(any())).thenReturn(spec);
    when(spec.data(any())).thenReturn(spec);
    when(spec.send()).thenReturn(Mono.empty());
    when(spec.retrieveMono(WebpbMessage.class)).thenReturn(Mono.just(mock(WebpbMessage.class)));
    return requester;
  }

  @Nested
  @DisplayName("put")
  class Put {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then put")
      void thenPut() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mock(RSocketRequester.class);
        requesters.put(UID_1, requester);
        assertThat(requesters.getRequester(UID_1)).isEqualTo(requester);
      }
    }
  }

  @Nested
  @DisplayName("remove")
  class Remove {

    @Nested
    @DisplayName("when equals to prev")
    class WhenEqualsToPrev {

      @Test
      @DisplayName("then remove")
      void thenRemove() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mock(RSocketRequester.class);
        requesters.put(UID_1, requester);
        requesters.remove(UID_1, requester);
        assertThat(requesters.getRequester(UID_1)).isNull();
      }
    }

    @Nested
    @DisplayName("when not equals to prev")
    class WhenNotEqualsToPrev {

      @Test
      @DisplayName("then not remove")
      void thenNotRemove() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mock(RSocketRequester.class);
        requesters.put(UID_1, requester);
        requesters.remove(UID_1, mock(RSocketRequester.class));
        assertThat(requesters.getRequester(UID_1)).isEqualTo(requester);
      }
    }
  }

  @Nested
  @DisplayName("fireAndForget")
  class FireAndForget {

    @Nested
    @DisplayName("when requester not found")
    class WhenRequesterNotFound {

      @Test
      @DisplayName("then not send")
      void thenNotSend() {
        Requesters requesters = new Requesters();
        WebpbMessage message = mock(WebpbMessage.class);
        requesters.fireAndForget(UID_1, message);
        verify(message, never()).webpbMeta();
      }
    }

    @Nested
    @DisplayName("when requester found")
    class WhenRequesterFound {

      @Test
      @DisplayName("then send")
      void thenSend() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mockRequester();
        requesters.put(UID_1, requester);
        WebpbMessage message = mock(WebpbMessage.class);
        when(message.webpbMeta()).thenReturn(mock(WebpbMeta.class));
        requesters.fireAndForget(UID_1, message);
        verify(message, times(1)).webpbMeta();
        verify(requester, times(1)).route(any());
      }
    }
  }

  @Nested
  @DisplayName("broadcast")
  class Broadcast {

    @Nested
    @DisplayName("when broadcast")
    class WhenBroadcast {

      @Test
      @DisplayName("then send to all")
      void thenSendToAll() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mockRequester();
        requesters.put(UID_1, requester);
        requesters.put(UID_2, requester);
        WebpbMessage message = mock(WebpbMessage.class);
        when(message.webpbMeta()).thenReturn(mock(WebpbMeta.class));
        requesters.broadcast(message);
        verify(message, times(1)).webpbMeta();
        verify(requester, times(2)).route(any());
      }
    }
  }

  @Nested
  @DisplayName("multicast")
  class Multicast {

    @Nested
    @DisplayName("when ids is empty")
    class WhenIdsIsEmpty {

      @Test
      @DisplayName("then not send")
      void thenNotSend() {
        Requesters requesters = new Requesters();
        WebpbMessage message = mock(WebpbMessage.class);
        requesters.multicast(Collections.emptyList(), message);
        verify(message, never()).webpbMeta();
      }
    }

    @Nested
    @DisplayName("when ids is not empty")
    class WhenIdsIsNotEmpty {

      @Test
      @DisplayName("then send")
      void thenSend() {
        Requesters requesters = new Requesters();
        RSocketRequester requester1 = mockRequester();
        requesters.put(UID_1, requester1);
        RSocketRequester requester2 = mockRequester();
        requesters.put(UID_2, requester2);
        WebpbMessage message = mock(WebpbMessage.class);
        when(message.webpbMeta()).thenReturn(mock(WebpbMeta.class));
        requesters.multicast(List.of(UID_1, UID_3), message);
        verify(message, times(1)).webpbMeta();
        verify(requester1, times(1)).route(any());
        verify(requester2, never()).route(any());
      }
    }
  }

  @Nested
  @DisplayName("requestResponse")
  class RequestResponse {

    @Nested
    @DisplayName("when requester not found")
    class WhenRequesterNotFound {

      @Test
      @DisplayName("then not send")
      void thenNotSend() {
        Requesters requesters = new Requesters();
        WebpbMessage message = mock(WebpbMessage.class);
        assertThatThrownBy(() -> requesters.requestResponse(UID_1, message, WebpbMessage.class))
            .isInstanceOf(BusinessException.class)
            .extracting("code")
            .isEqualTo(ErrorCode.PLAYER_IS_OFFLINE);
        verify(message, never()).webpbMeta();
      }
    }

    @Nested
    @DisplayName("when requester found")
    class WhenRequesterFound {

      @Test
      @DisplayName("then send")
      void thenSend() {
        Requesters requesters = new Requesters();
        RSocketRequester requester = mockRequester();
        requesters.put(UID_1, requester);
        WebpbMessage message = mock(WebpbMessage.class);
        when(message.webpbMeta()).thenReturn(mock(WebpbMeta.class));
        assertThat(requesters.requestResponse(UID_1, message, WebpbMessage.class)).isNotNull();
        verify(message, times(1)).webpbMeta();
        verify(requester, times(1)).route(any());
      }
    }
  }
}
