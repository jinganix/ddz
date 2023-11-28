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

import { Deferred } from "./deferred";

export class Replay<T> {
  private resolvedAt = 0;

  private deferred: Deferred<T> | null = null;

  private key = "";

  constructor(value: T | null) {
    if (value) {
      this.deferred = new Deferred<T>();
      this.deferred.resolve(value);
    }
  }

  async resolve(promise: () => Promise<T>, key = ""): Promise<T> {
    if (!this.deferred || this.key != key) {
      this.key = key;
      this.deferred = new Deferred<T>();
      const value = await promise();
      this.deferred.resolve(value);
      this.resolvedAt = Date.now();
    }
    return this.deferred.promise;
  }

  async value(): Promise<T | null> {
    return !this.deferred ? null : this.deferred.promise;
  }

  get resolved(): boolean {
    return this.resolvedAt > 0;
  }
}
