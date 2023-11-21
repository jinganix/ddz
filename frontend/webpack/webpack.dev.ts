import { Configuration } from "webpack";
import { merge } from "webpack-merge";
import common from "./webpack.common";

const config = (env: string | { dev: boolean }): Configuration => {
  return merge(common(env), {
    devServer: {
      compress: true,
      historyApiFallback: true,
      port: 4300,
    },
    devtool: "source-map",
    mode: "development",
    stats: "errors-warnings",
  });
};

export default config;
