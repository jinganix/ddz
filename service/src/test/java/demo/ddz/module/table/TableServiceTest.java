/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
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

package demo.ddz.module.table;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import demo.ddz.helper.phase.PhaseExecutors;
import demo.ddz.module.phase.DdzPhaseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("TableService")
@ExtendWith(MockitoExtension.class)
class TableServiceTest {

  @Mock PhaseExecutors phaseExecutors;

  @InjectMocks TableService tableService;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("when execute")
      void ThenExecute() {
        Table table = new Table();
        tableService.execute(table, DdzPhaseType.IDLE);
        verify(phaseExecutors, times(1)).execute(table.getPhase(), DdzPhaseType.IDLE);
      }
    }
  }
}
