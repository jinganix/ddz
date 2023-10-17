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

package io.github.jinganix.ddz.setup.configuration;

import io.github.jinganix.ddz.module.auth.model.GrantedRole;
import io.github.jinganix.ddz.setup.argument.messaging.PlayerIdArgumentResolver;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.Encoder;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodReturnValueHandler;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.messaging.rsocket.annotation.support.RSocketPayloadReturnValueHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import reactor.core.publisher.Mono;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class RsocketConfiguration {

  static class PayloadReturnValueHandler extends RSocketPayloadReturnValueHandler {

    public PayloadReturnValueHandler(List<Encoder<?>> encoders, ReactiveAdapterRegistry registry) {
      super(encoders, registry);
    }

    @Override
    public Mono<Void> handleReturnValue(
        Object returnValue, MethodParameter returnType, Message<?> message) {
      return super.handleReturnValue(returnValue, returnType, message);
    }
  }

  static class MessageHandler extends RSocketMessageHandler {

    @Override
    @SuppressWarnings("unchecked")
    protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
      List<HandlerMethodReturnValueHandler> handlers =
          (List<HandlerMethodReturnValueHandler>) super.initReturnValueHandlers();
      handlers.set(
          0,
          new PayloadReturnValueHandler(
              (List<Encoder<?>>) getEncoders(), getReactiveAdapterRegistry()));
      return handlers;
    }
  }

  @Bean
  public RSocketMessageHandler messageHandler(
      RSocketStrategies rSocketStrategies,
      ObjectProvider<RSocketMessageHandlerCustomizer> customizers) {
    RSocketMessageHandler messageHandler = new MessageHandler();
    messageHandler
        .getArgumentResolverConfigurer()
        .addCustomResolver(new PlayerIdArgumentResolver());
    messageHandler.setRSocketStrategies(rSocketStrategies);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(messageHandler));
    return messageHandler;
  }

  @Bean
  PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
    rsocket
        .authorizePayload(
            authorize ->
                authorize
                    .setup()
                    .hasRole(GrantedRole.PLAYER.getValue())
                    .anyExchange()
                    .authenticated())
        .simpleAuthentication(Customizer.withDefaults());
    return rsocket.build();
  }

  @Getter
  @RequiredArgsConstructor
  static class SuccessMessage {

    private final int code = 0;

    private final Object data;
  }
}
