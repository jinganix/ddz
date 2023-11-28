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

import { AuthToken, deleteAuthToken, readAuthToken, saveAuthToken } from "@/lib/token";
import { emitter } from "@/lib/event/emitter";

describe("AuthToken", () => {
  describe("isExpired", () => {
    describe("when token is expired", () => {
      it("then return true", () => {
        const token = new AuthToken();
        expect(token.isExpired()).toBeTruthy();
      });
    });

    describe("when token is not expired", () => {
      it("then return false", () => {
        const token = new AuthToken();
        token.createdAt = Number.MAX_SAFE_INTEGER;
        expect(token.isExpired()).toBeFalsy();
      });
    });
  });
});

describe("readAuthToken", () => {
  beforeEach(() => localStorage.removeItem("AUTH_TOKEN"));

  describe("when data not exists", () => {
    it("then return null", () => {
      expect(readAuthToken()).toBeNull();
    });
  });

  describe("when data is invalid", () => {
    it("then return null", () => {
      localStorage.setItem("AUTH_TOKEN", "hello");
      expect(readAuthToken()).toBeNull();
    });
  });

  describe("when data is valid", () => {
    it("then return token", () => {
      const expected = {
        accessToken: "test_token",
        createdAt: 123,
        expiresIn: 234,
        groups: ["a"],
        refreshToken: "test_refresh_token",
      };
      localStorage.setItem("AUTH_TOKEN", JSON.stringify(expected));
      const token = readAuthToken();
      expect(token).toEqual(expected);
    });
  });
});

describe("saveAuthToken", () => {
  beforeEach(() => localStorage.removeItem("AUTH_TOKEN"));

  describe("when save token", () => {
    it("then emit event", () => {
      jest.spyOn(Storage.prototype, "setItem");
      const emitSpy = jest.spyOn(emitter, "emit");
      const token = new AuthToken();
      token.accessToken = "test_token";

      saveAuthToken(token);

      expect(localStorage.setItem).toHaveBeenCalledWith("AUTH_TOKEN", JSON.stringify(token));
      expect(emitSpy).toHaveBeenCalledWith("token", token);
    });
  });
});

describe("deleteAuthToken", () => {
  beforeEach(() => localStorage.removeItem("AUTH_TOKEN"));

  describe("when delete token", () => {
    it("then emit event", () => {
      jest.spyOn(Storage.prototype, "removeItem");
      const emitSpy = jest.spyOn(emitter, "emit");

      deleteAuthToken();

      expect(localStorage.removeItem).toHaveBeenCalledWith("AUTH_TOKEN");
      expect(emitSpy).toHaveBeenCalledWith("token", null);
    });
  });
});
