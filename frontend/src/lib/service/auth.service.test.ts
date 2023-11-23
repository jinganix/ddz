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

import { AuthTokenResponse } from "@proto/AuthProto";
import { AuthService } from "@/lib/service/auth.service";
import { emitter } from "@/lib/event/emitter";
import * as request from "@/lib/service/request";
import * as token from "@/lib/token";
import { AuthToken } from "@/lib/token";

describe("AuthService", () => {
  beforeEach(() => jest.useFakeTimers().setSystemTime(12345678));

  afterEach(() => jest.restoreAllMocks());

  const RESPONSE_DATA = {
    accessToken: "test_token",
    expiresIn: 1000,
    refreshToken: "test_refresh_token",
    scope: "a,b,c",
    tokenType: "Bearer",
  };

  const TOKEN = (() => {
    const token = new AuthToken();
    token.accessToken = RESPONSE_DATA.accessToken;
    token.expiresIn = RESPONSE_DATA.expiresIn;
    token.refreshToken = RESPONSE_DATA.refreshToken;
    token.groups = ["a", "b", "c"];
    token.createdAt = 12345678;
    return token;
  })();

  describe("constructor", () => {
    describe("when concrete", () => {
      it("then check refresh periodically", async () => {
        const authService = new AuthService();
        const spy = jest.spyOn(authService, "checkRefresh").mockImplementation(async () => {});
        jest.advanceTimersByTime(10000);
        expect(spy).toHaveBeenCalled();
      });
    });
  });

  describe("checkRefresh", () => {
    describe("when token is null", () => {
      it("then not refresh", async () => {
        const authService = new AuthService();
        const refreshSpy = jest.spyOn(authService, "refresh");

        await authService.checkRefresh();
        expect(refreshSpy).not.toHaveBeenCalled();
      });
    });

    describe("when token is expired", () => {
      it("then refresh", async () => {
        localStorage.setItem("AUTH_TOKEN", JSON.stringify({ ...TOKEN, createdAt: 0 }));
        const authService = new AuthService();
        const refreshSpy = jest.spyOn(authService, "refresh").mockImplementation(async () => null);

        await authService.checkRefresh();
        expect(refreshSpy).toHaveBeenCalled();
      });
    });

    describe("when token is not expired", () => {
      it("then not refresh", async () => {
        localStorage.setItem("AUTH_TOKEN", JSON.stringify(TOKEN));
        const authService = new AuthService();
        const refreshSpy = jest.spyOn(authService, "refresh");

        await authService.checkRefresh();
        expect(refreshSpy).not.toHaveBeenCalled();
      });
    });
  });

  describe("auth", () => {
    describe("when request successfully", () => {
      it("then token is retrieved", async () => {
        jest.spyOn(request, "request").mockResolvedValue(AuthTokenResponse.create(RESPONSE_DATA));
        const saveSpy = jest.spyOn(token, "saveAuthToken");
        const emitSpy = jest.spyOn(emitter, "emit");

        const authService = new AuthService();
        await authService.auth("aaa", "aaa");

        expect(saveSpy).toHaveBeenCalledWith(TOKEN);
        expect(emitSpy).toHaveBeenCalledWith("notifier", "success", "Login successful");
      });
    });

    describe("when request failed", () => {
      it("then log error", async () => {
        jest.spyOn(request, "request").mockRejectedValue(new Error("error"));
        const errorSpy = jest.spyOn(console, "error").mockImplementation(() => {});
        const saveSpy = jest.spyOn(token, "saveAuthToken");
        const emitSpy = jest.spyOn(emitter, "emit");

        const authService = new AuthService();
        await authService.auth("aaa", "aaa");

        expect(saveSpy).not.toHaveBeenCalled();
        expect(emitSpy).not.toHaveBeenCalled();
        expect(errorSpy).toHaveBeenCalledWith(new Error("error"));
      });
    });
  });

  describe("getToken", () => {
    describe("when token not exists", () => {
      it("then return null", async () => {
        localStorage.removeItem("AUTH_TOKEN");

        const authService = new AuthService();
        expect(await authService.getToken()).toBeNull();
      });
    });

    describe("when token is not expired", () => {
      it("then return the token", async () => {
        localStorage.setItem("AUTH_TOKEN", JSON.stringify(TOKEN));

        const authService = new AuthService();
        expect(await authService.getToken()).toEqual(TOKEN);
      });
    });

    describe("when token is expired", () => {
      describe("when refresh successfully", () => {
        it("then return the token", async () => {
          jest.spyOn(request, "request").mockResolvedValue(AuthTokenResponse.create(RESPONSE_DATA));
          localStorage.setItem("AUTH_TOKEN", JSON.stringify({ ...TOKEN, createdAt: 0 }));

          const authService = new AuthService();
          expect(await authService.getToken()).toEqual(TOKEN);
        });
      });

      describe("when refresh failed", () => {
        it("then return the token", async () => {
          const logSpy = jest.spyOn(console, "log").mockImplementation(() => {});
          jest.spyOn(request, "request").mockRejectedValue(new Error("error"));
          localStorage.setItem("AUTH_TOKEN", JSON.stringify({ ...TOKEN, createdAt: 0 }));

          const authService = new AuthService();
          expect(await authService.getToken()).toBeNull();
          expect(logSpy).toHaveBeenCalledWith(new Error("error"));
        });
      });
    });
  });
});
