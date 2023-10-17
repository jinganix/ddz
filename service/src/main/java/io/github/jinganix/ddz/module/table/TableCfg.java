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

package io.github.jinganix.ddz.module.table;

import static io.github.jinganix.ddz.module.phase.DdzPhaseType.BIDDING;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.COUNTDOWN;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.DEALING;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.DOUBLING;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.END;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.IDLE;
import static io.github.jinganix.ddz.module.phase.DdzPhaseType.SETTLEMENT;

import io.github.jinganix.ddz.module.phase.DdzPhaseType;

public class TableCfg {

  // TODO: replace with switch statement once jacoco fix the coverage issue
  public long getDuration(DdzPhaseType phaseType) {
    if (phaseType == IDLE || phaseType == SETTLEMENT || phaseType == END) {
      return 0;
    } else if (phaseType == COUNTDOWN
        || phaseType == DEALING
        || phaseType == BIDDING
        || phaseType == DOUBLING) {
      return 3000;
    } else {
      return 15000;
    }
  }
}
