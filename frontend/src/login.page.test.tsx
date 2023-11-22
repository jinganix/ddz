import React from "react";
import { act, fireEvent, render, screen } from "@testing-library/react";
import { toast } from "react-toastify";
import { LoginPage } from "@/login.page";

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

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("Please enter a username");
      });
    });

    describe("when username length < 3", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aa" } }),
        );

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("The minimum username length is 3 characters");
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

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("The maximum username length is 20 characters");
      });
    });

    describe("when password is missing", () => {
      it("then show error toast", async () => {
        await act(() => render(<LoginPage />));
        await act(() =>
          fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
        );

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("Please enter a password");
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

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("The minimum password length is 6 characters");
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

        const toastError = jest.spyOn(toast, "error");
        fireEvent.click(screen.getByText("SignIn or SignUp"));

        expect(toastError).toHaveBeenCalledWith("The maximum password length is 20 characters");
      });
    });
  });

  describe("when input is valid", () => {
    it("then submit the form", async () => {
      const logSpy = jest.spyOn(console, "log");

      await act(() => render(<LoginPage />));
      await act(() =>
        fireEvent.input(screen.getByPlaceholderText("Username"), { target: { value: "aaa" } }),
      );
      await act(() =>
        fireEvent.input(screen.getByPlaceholderText("Password"), { target: { value: "aaaaaa" } }),
      );

      await act(() => fireEvent.click(screen.getByText("SignIn or SignUp")));
      expect(logSpy).toHaveBeenCalledWith(["aaa", "aaaaaa"]);
    });
  });
});
