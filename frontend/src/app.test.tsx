import { render, screen } from "@testing-library/react";
import App from "./app";

describe("App", () => {
  describe("when render", () => {
    it("then text exists", () => {
      render(<App />);
      const element = screen.getByText(/Hello world/i);
      expect(element).toBeInTheDocument();
    });
  });
});
