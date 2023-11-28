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

type Callback = {
  (...args: unknown[]): void | Promise<void>;
  order: number;
};

interface EventsMap {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [event: string]: any;
}

interface DefaultEvents extends EventsMap {
  [event: string]: Callback;
}

export interface Unsubscribe {
  (): void;
}

export class Emitter<Events extends EventsMap = DefaultEvents> {
  events: Partial<{ [E in keyof Events]: Events[E][] }> = {};

  on<K extends keyof Events>(event: K, cb: Events[K], order?: number): Unsubscribe {
    cb.order = order;
    this.events[event]?.push(cb) || (this.events[event] = [cb]);
    this.events[event]?.sort((a, b) => a.order - b.order);
    return () => {
      this.events[event] = this.events[event]?.filter((x) => cb !== x);
    };
  }

  async emit<K extends keyof Events>(event: K, ...args: Parameters<Events[K]>): Promise<void> {
    const callbacks = this.events[event] || [];
    for (let i = 0, length = callbacks.length; i < length; i++) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      await callbacks[i](...args);
    }
  }
}

export function createNanoEvents<T extends EventsMap = DefaultEvents>(): Emitter<T> {
  return new Emitter<T>();
}
