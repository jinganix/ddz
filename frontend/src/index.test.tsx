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
