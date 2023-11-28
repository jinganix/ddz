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

import { Configuration } from "webpack";
import { merge } from "webpack-merge";
import common from "./webpack.common";

const config = (env: string | { dev: boolean }): Configuration => {
  return merge(common(env), {
    devServer: {
      compress: true,
      historyApiFallback: true,
      port: 4300,
    },
    devtool: "source-map",
    mode: "development",
    stats: "errors-warnings",
  });
};

export default config;
