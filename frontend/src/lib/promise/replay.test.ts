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

import { Replay } from "@/lib/promise/replay";

describe("Replay", () => {
  describe("resolve", () => {
    describe("when keys are same", () => {
      it("then return first", async () => {
        const replay = new Replay<number>();

        expect(await replay.resolve(() => Promise.resolve(1), "key")).toEqual(1);
        expect(await replay.resolve(() => Promise.resolve(2), "key")).toEqual(1);
      });
    });

    describe("when keys are different", () => {
      it("then return each", async () => {
        const replay = new Replay<number>();

        expect(await replay.resolve(() => Promise.resolve(1), "key1")).toEqual(1);
        expect(await replay.resolve(() => Promise.resolve(2), "key2")).toEqual(2);
      });
    });
  });

  describe("value", () => {
    describe("when deferred is null", () => {
      it("then return null", async () => {
        expect(await new Replay<number>().value()).toBeNull();
      });
    });

    describe("when deferred is not null", () => {
      it("then return resolved value", async () => {
        const replay = new Replay<number>();
        await replay.resolve(() => Promise.resolve(1));

        expect(await replay.value()).toEqual(1);
      });
    });
  });

  describe("resolved", () => {
    describe("when replay is not resolved", () => {
      it("then return false", async () => {
        expect(new Replay<number>().resolved).toBeFalsy();
      });
    });

    describe("when replay is resolved", () => {
      it("then return true", async () => {
        const replay = new Replay<number>();
        await replay.resolve(() => Promise.resolve(1));

        expect(replay.resolved).toBeTruthy();
      });
    });
  });
});
