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

package demo.ddz.module.phase.executor;

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.module.phase.DdzPhaseType;
import demo.ddz.module.table.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("EndExecutor")
@ExtendWith(MockitoExtension.class)
class EndExecutorTest {

  @InjectMocks EndExecutor endExecutor;

  @Nested
  @DisplayName("getPhaseType")
  class GetPhaseType {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return END")
      void thenReturn() {
        assertThat(endExecutor.getPhaseType()).isEqualTo(DdzPhaseType.END);
      }
    }
  }

  @Nested
  @DisplayName("schedule")
  class Schedule {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return 0")
      void thenReturn() {
        assertThat(endExecutor.schedule(new Table())).isEqualTo(0);
      }
    }
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return IDLE")
      void thenReturn() {
        assertThat(endExecutor.execute(new Table())).isEqualTo(DdzPhaseType.IDLE);
      }
    }
  }
}
