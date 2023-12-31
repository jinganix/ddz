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

package io.github.jinganix.ddz.helper.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {

  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  public int nextInt(int origin, int bound) {
    return ThreadLocalRandom.current().nextInt(origin, bound);
  }

  /**
   * Generate uuid.
   *
   * @param dash true with dash
   * @return uuid
   */
  public synchronized String uuid(boolean dash) {
    if (dash) {
      return UUID.randomUUID().toString();
    } else {
      return UUID.randomUUID().toString().replace("-", "");
    }
  }
}
