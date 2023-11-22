import React, { FC } from "react";
import { ToastContainer } from "react-toastify";
import { LoginPage } from "./login.page";
import "react-toastify/dist/ReactToastify.css";
import "./app.scss";

const App: FC = () => (
  <div data-testid="test-app">
    <LoginPage />
    <ToastContainer
      position="top-right"
      autoClose={3000}
      hideProgressBar
      newestOnTop={false}
      closeOnClick
      rtl={false}
      pauseOnFocusLoss={false}
      draggable={false}
      pauseOnHover
      theme="dark"
    />
  </div>
);

export default App;
