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
import { act, fireEvent, render, screen } from "@testing-library/react";
import { toast } from "react-toastify";
import { LoginPage } from "./login.page";
import { container } from "tsyringe";
import { AuthService } from "@/lib/service/auth.service";

describe("LoginPage", () => {
  describe("when render", () => {
    it("then the element exists", async () => {
      await act(() => render(<LoginPage />));

      expect(screen.getByPlaceholderText("Username")).toBeInTheDocument();
      expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
      expect(screen.getByText("SignIn or SignUp")).toBeInTheDocument();
    });
  });

  describe("when input is invalid", () => {
    describe("when username is missing", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("Please enter a username");
      });
    });

    describe("when username length < 3", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aa" } }),
        );

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("The minimum username length is 3 characters");
      });
    });

    describe("when username length > 20", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), {
            target: { value: "aaaaaaaaaaaaaaaaaaaaa" },
          }),
        );

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("The maximum username length is 20 characters");
      });
    });

    describe("when password is missing", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
        );

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("Please enter a password");
      });
    });

    describe("when password length < 6", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
        );
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Password"), { target: { value: "aaaaa" } }),
        );

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("The minimum password length is 6 characters");
      });
    });

    describe("when password length > 20", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
        );
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Password"), {
            target: { value: "aaaaaaaaaaaaaaaaaaaaa" },
          }),
        );

        const toastSpy = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastSpy).toHaveBeenCalledWith("The maximum password length is 20 characters");
      });
    });
  });

  describe("when input is valid", () => {
    it("then submit the form", async () => {
      const authSpy = jest.spyOn(container.resolve(AuthService), "auth").mockResolvedValue();

      await act(() => render(<LoginPage />));
      await act(() =>
        fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
      );
      await act(() =>
        fireEvent.input(screen.getByPlaceholderText("Password"), { target: { value: "aaaaaa" } }),
      );

      await act(() => fireEvent.click(screen.getByText("SignIn or SignUp")));
      expect(authSpy).toHaveBeenCalledWith("aaa", "aaaaaa");
    });
  });
});
