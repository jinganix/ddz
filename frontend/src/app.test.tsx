import React from "react";
import { act, render, screen } from "@testing-library/react";
import App from "./app";

describe("App", () => {
  describe("when render", () => {
    it("then the element exists", async () => {
      await act(() => render(<App />));
      const element = screen.getByTestId("test-app");
      expect(element).toBeInTheDocument();
    });
  });
});
