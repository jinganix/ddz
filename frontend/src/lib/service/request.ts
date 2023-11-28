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

import { WebpbMessage, WebpbMeta } from "webpb";
import axios, { AxiosError } from "axios";
import urlJoin from "url-join";
import { environment } from "@/lib/environment";
import { emitter } from "@/lib/event/emitter";

export type ResponseError = {
  code?: string;
  emitted: boolean;
  status: number;
};

export interface RequestConfig<R extends WebpbMessage> {
  message: WebpbMessage;
  responseType?: { prototype: R };
  host: string;
  headers: Record<string, string>;
}

function formatUrl(meta: WebpbMeta, url?: string): string {
  const baseUrl = (url ? url : environment.apiHost) || "";
  const context = meta.context || "";
  const path = meta.path || "";
  return urlJoin(baseUrl, context, path);
}

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";

async function checkError(status: number, data: Record<string, unknown>): Promise<void> {
  if (status >= 200 && status < 300) {
    return;
  }

  if (status == 401) {
    throw { emitted: false, status } as ResponseError;
  }
  if (status == 403) {
    await emitter.emit("notifier", "error", "Forbidden");
  } else if (status == 404) {
    await emitter.emit("notifier", "error", "Not Found");
  } else if (data?.code) {
    await emitter.emit("notifier", "error", String(data.code));
  } else {
    await emitter.emit("notifier", "error", "Request failed, please try again");
  }
  throw { code: data?.code, emitted: true, status } as ResponseError;
}

export async function request<R extends WebpbMessage>(config: RequestConfig<R>): Promise<R> {
  const meta = config.message.webpbMeta();
  const url = formatUrl(meta, config.host);
  let res;
  try {
    res = await axios.request({
      data: config.message.toWebpbAlias() as Record<string, unknown>,
      headers: config.headers,
      method: meta.method,
      responseType: "json",
      timeout: 5000,
      url,
    });
  } catch (err) {
    res = (err as AxiosError).response;
    if (!res) {
      const code = (err as { code: string })?.code;
      if (code) {
        await emitter.emit("notifier", "error", code);
        throw { code, emitted: true } as ResponseError;
      }
      throw err;
    }
  }
  const data = res.data as Record<string, unknown>;
  await checkError(res.status, data);
  const responseType = config.responseType as unknown as {
    fromAlias: (data: unknown) => R;
  };
  const { fromAlias } = responseType || {};
  return fromAlias ? fromAlias(data) : (data as R);
}
