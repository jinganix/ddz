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

import React from "react";
import App from "./app";

const render = jest.fn();
jest.doMock("react-dom/client", () => ({ createRoot: () => ({ render: render }) }));

describe("Index", () => {
  describe("when render", () => {
    it("then rendered without error", () => {
      const div = document.createElement("div");
      div.id = "root";
      document.body.appendChild(div);
      require("./index.tsx");
      expect(render).toHaveBeenCalledWith(
        <React.StrictMode>
          <App />
        </React.StrictMode>,
      );
    });
  });
});
