import React from "react";
import { act, render, screen } from "@testing-library/react";
import { Button } from "./button";

describe("Button", () => {
  describe("when render", () => {
    it("then the element exists", async () => {
      await act(() => render(<Button data-testid="test-button" />));
      const element = screen.getByTestId("test-button");
      expect(element).toBeInTheDocument();
    });
  });

  describe("when render as child", () => {
    it("then the element exists", async () => {
      await act(() =>
        render(
          <Button asChild data-testid="test-button">
            <div>Hello</div>
          </Button>,
        ),
      );
      const element = screen.getByTestId("test-button");
      expect(element).toBeInTheDocument();
    });
  });
});
