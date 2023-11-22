import React from "react";
import { act, render, screen } from "@testing-library/react";
import { Input } from "./input";

describe("Input", () => {
  describe("when render", () => {
    it("then the element exists", async () => {
      await act(() => render(<Input data-testid="test-button" />));
      const element = screen.getByTestId("test-button");
      expect(element).toBeInTheDocument();
    });
  });
});
