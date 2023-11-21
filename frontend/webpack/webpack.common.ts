import * as path from "path";
import { Configuration as WebpackConfiguration, ProvidePlugin, RuleSetUseItem } from "webpack";
import { Configuration as WebpackDevServerConfiguration } from "webpack-dev-server";
import TsconfigPathsPlugin from "tsconfig-paths-webpack-plugin";
import ESLintWebpackPlugin from "eslint-webpack-plugin";
import HtmlWebpackPlugin from "html-webpack-plugin";
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import { WebpackManifestPlugin } from "webpack-manifest-plugin";
import TerserPlugin from "terser-webpack-plugin";
import CssMinimizerPlugin from "css-minimizer-webpack-plugin";

interface Configuration extends WebpackConfiguration {
  devServer?: WebpackDevServerConfiguration;
}

const cssRegex = /\.css$/;
const cssModuleRegex = /\.module\.css$/;
const sassRegex = /\.(scss|sass)$/;
const sassModuleRegex = /\.module\.(scss|sass)$/;

const common = (env: string | { dev: boolean }): Configuration => {
  const isDev: boolean = typeof env === "string" ? env !== "prod" : env.dev;
  const isProd = !isDev;

  const getStyleLoaders = (
    cssOptions: Record<string, unknown>,
    preProcessor?: string,
  ): RuleSetUseItem[] => {
    const loaders = [
      isDev && require.resolve("style-loader"),
      isProd && {
        loader: MiniCssExtractPlugin.loader,
      },
      {
        loader: require.resolve("css-loader"),
        options: cssOptions,
      },
      {
        loader: require.resolve("postcss-loader"),
        options: {
          postcssOptions: {
            config: false,
            ident: "postcss",
            plugins: [
              "tailwindcss",
              "postcss-flexbugs-fixes",
              [
                "postcss-preset-env",
                {
                  autoprefixer: {
                    flexbox: "no-2009",
                  },
                  stage: 3,
                },
              ],
            ],
          },
          sourceMap: isProd,
        },
      },
    ].filter(Boolean);
    if (preProcessor) {
      loaders.push(
        {
          loader: require.resolve("resolve-url-loader"),
          options: {
            root: "",
            sourceMap: isProd,
          },
        },
        {
          loader: require.resolve(preProcessor),
          options: {
            sourceMap: true,
          },
        },
      );
    }
    return loaders as RuleSetUseItem[];
  };

  return {
    entry: ["./src/index.tsx"],
    module: {
      rules: [
        {
          include: [path.resolve(__dirname, "../src"), path.resolve(__dirname, "../build")],
          test: /\.(ts|js)x?$/,
          use: [
            {
              loader: "babel-loader",
              options: {
                presets: ["@babel/preset-env", "@babel/preset-react", "@babel/preset-typescript"],
              },
            },
          ],
        },
        {
          exclude: cssModuleRegex,
          sideEffects: true,
          test: cssRegex,
          use: getStyleLoaders({
            importLoaders: 1,
            modules: {
              mode: "icss",
            },
            sourceMap: isProd,
          }),
        },
        {
          exclude: sassModuleRegex,
          sideEffects: true,
          test: sassRegex,
          use: getStyleLoaders(
            {
              importLoaders: 3,
              modules: {
                mode: "icss",
              },
              sourceMap: isProd,
            },
            "sass-loader",
          ),
        },
        {
          test: sassModuleRegex,
          use: getStyleLoaders(
            {
              importLoaders: 3,
              modules: {
                mode: "local",
              },
              sourceMap: true,
            },
            "sass-loader",
          ),
        },
      ],
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            compress: {
              comparisons: false,
              ecma: 5,
              inline: 2,
            },
            mangle: {
              safari10: true,
            },
            output: {
              ascii_only: true,
              comments: false,
              ecma: 5,
            },
          },
        }),
        // This is only used in production mode
        new CssMinimizerPlugin(),
      ],
      splitChunks: {
        cacheGroups: {
          config: {
            chunks: "all",
            enforce: true,
            name: "config",
            test: /[\\/]src[\\/]configs[\\/].*\.(js|jsx|ts|tsx)$/,
          },
          generated: {
            chunks: "all",
            enforce: true,
            name: "generated",
            test: /[\\/]build[\\/]generated[\\/].*\.(js|jsx|ts|tsx)$/,
          },
          vendors: {
            chunks: "all",
            enforce: true,
            name: "vendors",
            test: /[\\/]node_modules[\\/]/,
          },
        },
        chunks: "async",
        enforceSizeThreshold: 50000,
        maxAsyncRequests: 30,
        maxInitialRequests: 30,
        minChunks: 1,
        minRemainingSize: 0,
        minSize: 20000,
      },
    },
    output: {
      chunkFilename: isDev ? "[name].chunk.js" : "[name].[contenthash:8].chunk.js",
      filename: isDev ? "[name].bundle.js" : "[name].[contenthash:8].bundle.js",
      path: path.resolve(__dirname, "../dist"),
      publicPath: "/",
    },
    plugins: [
      new HtmlWebpackPlugin(
        Object.assign(
          {
            appTitle: "DDZ",
            template: "public/index.html",
          },
          { inject: true },
          isDev
            ? undefined
            : {
                minify: {
                  collapseWhitespace: true,
                  keepClosingSlash: true,
                  minifyCSS: true,
                  minifyJS: true,
                  minifyURLs: true,
                  removeComments: true,
                  removeEmptyAttributes: true,
                  removeRedundantAttributes: true,
                  removeStyleLinkTypeAttributes: true,
                  useShortDoctype: true,
                },
              },
        ),
      ),
      new WebpackManifestPlugin({
        fileName: "asset-manifest.json",
        generate: (seed, files, entryPoints) => {
          const manifestFiles = files.reduce((manifest, file) => {
            manifest[file.name] = file.path;
            return manifest;
          }, seed);
          const entrypointFiles = entryPoints.main.filter((fileName) => !fileName.endsWith(".map"));
          return {
            entrypoints: entrypointFiles,
            files: manifestFiles,
          };
        },
        publicPath: "./public",
      }),
      new ProvidePlugin({
        Buffer: ["buffer", "Buffer"],
      }),
      new ESLintWebpackPlugin({
        emitError: true,
        emitWarning: true,
        exclude: ["build", "node_modules"],
        extensions: ["ts", "tsx"],
        failOnError: true,
        overrideConfigFile: ".eslintrc.js",
      }),
    ].filter(Boolean),
    resolve: {
      extensions: [".ts", ".tsx", ".js"],
      plugins: [new TsconfigPathsPlugin()],
      preferRelative: true,
    },
  };
};

export default common;
