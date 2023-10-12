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

package demo.ddz.module.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.ddz.proto.error.ErrorCode;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbMeta;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Mono;

@Slf4j
public class MessageListener {

  @Getter private final List<WebpbMessage> messages = new ArrayList<>();

  private final Map<String, Class<WebpbMessage>> destMap = new HashMap<>();

  private final ObjectMapper objectMapper = new ObjectMapper();

  @SuppressWarnings("unchecked")
  public MessageListener() {
    objectMapper.registerSubtypes(NamedTypes.NAMED_TYPES);
    ReflectionUtils.findAllClassesInPackage(
            ErrorCode.class.getPackageName().replace(".error", ""),
            ClassFilter.of(WebpbMessage.class::isAssignableFrom))
        .stream()
        .map(e -> (Class<WebpbMessage>) e)
        .forEach(
            classType -> {
              WebpbMeta webpbMeta = WebpbUtils.readWebpbMeta(classType);
              destMap.put(webpbMeta.getPath(), classType);
            });
  }

  @MessageMapping("{dest}")
  public Mono<Void> handle(@DestinationVariable("dest") String dest, @Payload String message)
      throws IOException {
    Class<WebpbMessage> classType = destMap.get(dest);
    messages.add(objectMapper.readValue(message, classType));
    return Mono.empty();
  }

  public List<WebpbMessage> filter(Class<?>... classTypes) {
    List<Class<?>> types = Arrays.asList(classTypes);
    return messages.stream().filter(e -> types.contains(e.getClass())).toList();
  }

  @SuppressWarnings("unchecked")
  public <T extends WebpbMessage> T getMessage(Class<T> type) {
    List<WebpbMessage> messages = filter(type);
    if (messages.size() > 1) {
      throw new RuntimeException("There are " + messages.size() + " messages of " + type.getName());
    }
    return messages.isEmpty() ? null : (T) messages.get(0);
  }

  public void clear() {
    this.messages.clear();
  }
}
