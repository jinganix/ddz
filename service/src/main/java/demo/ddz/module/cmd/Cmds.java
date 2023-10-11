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

package demo.ddz.module.cmd;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Cmds {

  private final List<Cmd> cmds = new ArrayList<>();

  private final List<CmdResult<? extends Cmd>> results = new ArrayList<>();

  private int index = 0;

  public Cmds() {}

  public Cmds(Cmd... cmds) {
    for (Cmd cmd : cmds) {
      this.push(cmd);
    }
  }

  public Cmds push(Cmd cmd) {
    this.cmds.add(cmd);
    return this;
  }

  public Cmds pushAll(List<Cmd> cmds) {
    this.cmds.addAll(cmds);
    return this;
  }

  public Cmd pop() {
    return isEmpty() ? null : cmds.get(index++);
  }

  public boolean isEmpty() {
    return index >= cmds.size();
  }

  public Cmds result(CmdResult<? extends Cmd> result) {
    this.results.add(result);
    return this;
  }
}
