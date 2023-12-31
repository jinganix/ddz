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

package io.github.jinganix.ddz.helper.uid;

/** Snowflake generator. */
public class Snowflake {

  private static final long SEQ_MAGIC = 1021;

  private static final long START_STAMP = 1577808000000L;

  private static final long SEQUENCE_BIT = 12;

  private static final long MACHINE_BIT = 10;

  private static final long DATACENTER_BIT = 0;

  private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

  private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

  private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

  private static final long MACHINE_LEFT = SEQUENCE_BIT;

  private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

  private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

  private final long datacenterId;

  private final long machineId;

  private long sequence = 0L;

  private long lastStamp = -1L;

  private Clock clock = System::currentTimeMillis;

  /**
   * Constructor.
   *
   * @param datacenterId data center Id
   * @param machineId machined Id
   */
  public Snowflake(long datacenterId, long machineId) {
    if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
      throw new IllegalArgumentException(
          String.format(
              "datacenterId can't be greater than %d or less than 0", MAX_DATACENTER_NUM));
    }
    if (machineId > MAX_MACHINE_NUM || machineId < 0) {
      throw new IllegalArgumentException(
          String.format("machineId can't be greater than %d or less than 0", MAX_MACHINE_NUM));
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  public void setClock(Clock clock) {
    this.clock = clock;
  }

  /**
   * Generate next uid.
   *
   * @return next uid
   */
  public synchronized long nextId() {
    long currStamp = currentTimeMillis();
    if (currStamp < lastStamp) {
      throw new RuntimeException("Clock moved backwards. Refusing to generate id");
    }

    if (currStamp == lastStamp) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == currStamp % SEQ_MAGIC) {
        currStamp = getNextMillis();
      }
    } else {
      sequence = currStamp % SEQ_MAGIC;
    }

    lastStamp = currStamp;
    return (currStamp - START_STAMP) << TIMESTAMP_LEFT
        | datacenterId << DATACENTER_LEFT
        | machineId << MACHINE_LEFT
        | sequence;
  }

  private long getNextMillis() {
    long millis = currentTimeMillis();
    while (millis <= lastStamp) {
      millis = currentTimeMillis();
    }
    return millis;
  }

  /**
   * Get current time in millis.
   *
   * @return current millis
   */
  public long currentTimeMillis() {
    return clock.currentTimeMillis();
  }
}
