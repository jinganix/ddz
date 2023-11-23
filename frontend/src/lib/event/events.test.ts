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
import { createNanoEvents } from "@/lib/event/events";

interface Emitter {
  test: (value: number) => void;
}

describe("Events", () => {
  describe("when no listeners", () => {
    it("then emit without errors", async () => {
      const emitter = createNanoEvents<Emitter>();

      await emitter.emit("test", 1);
    });
  });

  describe("when emit event", () => {
    it("then listener is called", async () => {
      const emitter = createNanoEvents<Emitter>();
      const fn = jest.fn();
      emitter.on("test", fn);

      await emitter.emit("test", 1);
      expect(fn).toHaveBeenCalledWith(1);
    });
  });

  describe("when listeners has order", () => {
    it("then call listeners by order", async () => {
      const emitter = createNanoEvents<Emitter>();
      const called: string[] = [];
      const fn1 = jest.fn().mockImplementation(() => called.push("fn1"));
      const fn2 = jest.fn().mockImplementation(() => called.push("fn2"));
      emitter.on("test", fn1, 100);
      emitter.on("test", fn2, 50);

      await emitter.emit("test", 0);
      expect(fn1).toHaveBeenCalledTimes(1);
      expect(fn1).toHaveBeenCalledTimes(1);
      expect(called).toEqual(["fn2", "fn1"]);
    });
  });

  describe("when unsubscribe", () => {
    it("then listener not called", async () => {
      const emitter = createNanoEvents<Emitter>();
      const fn = jest.fn();
      emitter.on("test", fn)();

      await emitter.emit("test", 0);
      expect(fn).not.toHaveBeenCalled();
    });
  });
});
