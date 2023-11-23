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
import { FC, useEffect } from "react";
import { toast, ToastContainer, ToastContent } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { ToastOptions } from "react-toastify/dist/types";
import { emitter, NotifierVariant } from "@/lib/event/emitter";

export const Notifier: FC = () => {
  useEffect(
    () =>
      emitter.on("notifier", (variant: NotifierVariant, content: string | ToastContent) => {
        const options: ToastOptions = {};
        switch (variant) {
          case "info":
            toast.info(content, options);
            break;
          case "error":
            toast.error(content, options);
            break;
          case "success":
            toast.success(content, options);
            break;
        }
      }),
    [],
  );

  return (
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
  );
};
