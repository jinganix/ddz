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

import { AuthLoginRequest, AuthTokenRequest, AuthTokenResponse } from "@proto/AuthProto";
import { singleton } from "tsyringe";
import { WebpbMessage } from "webpb";
import { v4 } from "uuid";
import { AuthToken, deleteAuthToken, readAuthToken, saveAuthToken } from "@/lib/token";
import { request } from "@/lib/service/request";
import { environment } from "@/lib/environment";
import { emitter } from "@/lib/event/emitter";
import { Replay } from "@/lib/promise/replay";

async function requestToken(message: WebpbMessage): Promise<AuthToken> {
  const res = await request({
    headers: {
      "content-type": "application/json;charset=utf-8",
    },
    host: environment.apiHost,
    message,
    responseType: AuthTokenResponse,
  });
  const token = new AuthToken();
  token.accessToken = res.accessToken;
  token.refreshToken = res.refreshToken;
  token.expiresIn = res.expiresIn;
  token.groups = res.scope.split(",").filter((x) => !!x);
  return token;
}

@singleton()
export class AuthService {
  private replay = new Replay<AuthToken>(readAuthToken());

  constructor() {
    setInterval(() => this.checkRefresh(), 10000);
  }

  async checkRefresh(): Promise<void> {
    try {
      const token = await this.replay.value();
      if (token && token.isExpired()) {
        await this.refresh(token.refreshToken);
      }
    } catch (_err) {}
  }

  async auth(username: string, password: string): Promise<void> {
    try {
      const token = await this.replay.resolve(
        () => requestToken(AuthLoginRequest.create({ password, username })),
        v4(),
      );
      await saveAuthToken(token);
      await emitter.emit("notifier", "success", "Login successful");
    } catch (err) {
      console.error(err);
    }
  }

  async getToken(): Promise<AuthToken | null> {
    const token = await this.replay.value();
    return token && token.isExpired() ? this.refresh(token.refreshToken) : token;
  }

  async refresh(refreshToken: string): Promise<AuthToken | null> {
    try {
      const token = await this.replay.resolve(
        () => requestToken(AuthTokenRequest.create({ refreshToken })),
        "refresh",
      );
      await saveAuthToken(token);
      return token;
    } catch (err) {
      console.log(err);
    }
    await deleteAuthToken();
    return null;
  }
}
