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

import io.github.jinganix.ddz.helper.actor.VirtualThreadExecutor;
import io.github.jinganix.ddz.helper.timer.MultiLevelWheelTimer;
import io.github.jinganix.ddz.helper.timer.TaskTimer;
import io.github.jinganix.peashooter.DefaultTracer;
import io.github.jinganix.peashooter.Tracer;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for beans. */
@Configuration
@RequiredArgsConstructor
public class BeansConfiguration {

  @Bean
  TaskTimer taskTimer() {
    return new MultiLevelWheelTimer(10, 5120, 2);
  }

  @Bean
  Tracer tracer() {
    return new DefaultTracer();
  }

  @Bean
  VirtualThreadExecutor virtualThreadExecutor() {
    return new VirtualThreadExecutor(Executors.newVirtualThreadPerTaskExecutor());
  }
}
