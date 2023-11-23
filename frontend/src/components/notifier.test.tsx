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
import { act, render, screen, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";
import { Notifier } from "@/components/notifier";
import { emitter } from "@/lib/event/emitter";

describe("Notifier", () => {
  describe("when emit info notification", () => {
    it("then show toast", async () => {
      const toastSpy = jest.spyOn(toast, "info");
      await act(() => render(<Notifier />));

      await emitter.emit("notifier", "info", "Hello world");
      expect(toastSpy).toHaveBeenCalledWith("Hello world", {});
      await waitFor(() => expect(screen.getByText("Hello world")).toBeInTheDocument());
    });
  });

  describe("when emit error notification", () => {
    it("then show toast", async () => {
      const toastSpy = jest.spyOn(toast, "error");
      await act(() => render(<Notifier />));

      await emitter.emit("notifier", "error", "Hello world");
      expect(toastSpy).toHaveBeenCalledWith("Hello world", {});
      await waitFor(() => expect(screen.getByText("Hello world")).toBeInTheDocument());
    });
  });

  describe("when emit success notification", () => {
    it("then show toast", async () => {
      const toastSpy = jest.spyOn(toast, "success");
      await act(() => render(<Notifier />));

      await emitter.emit("notifier", "success", "Hello world");
      expect(toastSpy).toHaveBeenCalledWith("Hello world", {});
      await waitFor(() => expect(screen.getByText("Hello world")).toBeInTheDocument());
    });
  });
});
